package playground.dhosse.bachelorarbeit;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.contrib.matsim4opus.utils.network.NetworkBoundaryBox;
import org.matsim.contrib.matsim4opus.utils.network.NetworkUtil;
import org.matsim.core.basic.v01.IdImpl;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.network.MatsimNetworkReader;
import org.matsim.core.network.NetworkImpl;
import org.matsim.core.network.algorithms.NetworkCleaner;
import org.matsim.core.population.MatsimPopulationReader;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.charts.BarChart;
import org.matsim.core.utils.geometry.geotools.MGC;
import org.matsim.core.utils.gis.ShapeFileReader;
import org.matsim.core.utils.gis.ShapeFileWriter;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;

public class NetworkInspector {//TODO pfade ändern
	
	private static Scenario scenario = null;
	
	private Map<Id,Double> distances = new HashMap<Id,Double>();
	
	private List<Link> lengthBelowStorageCapacity = new ArrayList<Link>();
	
	private Map<String,ArrayList<Node>> nodeTypes = new TreeMap<String,ArrayList<Node>>();
	
	private List<Node> deadEndNodes = new ArrayList<Node>();
	private List<Node> exitRoadNodes = new ArrayList<Node>();
	private List<Node> redundantNodes = new ArrayList<Node>();
	
	private Map<Id,ArrayList<Activity>> mappedPopulation = new HashMap<Id,ArrayList<Activity>>();
	
	private Logger logger = Logger.getLogger(NetworkInspector.class);
	
	private double[] linkCapacities = new double[6];
	
	private double[] nLanes = new double[6];
	
	private double[] inDegrees = new double[7];
	private double[] outDegrees = new double[7];
	
	private double[] lengths = new double[9];
	private double[] geoLengths = new double[9];
	
	private double totalLength = 0;
	private double totalGLength = 0;
	
	private boolean nodeAttributesChecked = false;
	
	private NetworkBoundaryBox bbox = new NetworkBoundaryBox();
	
	private String outputFolder = "C:/Users/Daniel/Dropbox/bsc";
	
	private SimpleFeatureBuilder builder;
	
	private Geometry area;
	
	/**
	 * 
	 * @param args (i) network file (ii) population file (optional)
	 */
	public static void main(String args[]){
		
		NetworkInspector.scenario = ScenarioUtils.createScenario(ConfigUtils.createConfig()); 
		
		if(args.length==1)
			new MatsimNetworkReader(NetworkInspector.scenario).readFile(args[0]);
		else if(args.length==2){
			new MatsimNetworkReader(NetworkInspector.scenario).readFile(args[0]);
			new MatsimPopulationReader(NetworkInspector.scenario).readFile(args[1]);
		}
		
		new NetworkInspector().run();
		
	}
	
	private void run() {
		
		boolean routable = isRoutable();
		checkNodeAttributes();
		checkLinkAttributes();
		
		if(!(NetworkInspector.scenario.getPopulation()==null))
			populationMapping();
		
		if(!routable)
			new NetworkCleaner().run(NetworkInspector.scenario.getNetwork());
		
//		ZoneLayer<Id> measuringPoints = GridUtils.createGridLayerByGridSizeByNetwork(800, this.bbox.getBoundingBox());
//		SpatialGrid freeSpeedGrid = new SpatialGrid(this.bbox.getBoundingBox(), 800);
//		ScenarioImpl sc = (ScenarioImpl) NetworkInspector.scenario;
//		
//		new AccessibilityCalc(measuringPoints, freeSpeedGrid, sc).runAccessibilityComputation();
		
	}

	public NetworkInspector(){
		
		this.bbox = new NetworkBoundaryBox();
		bbox.setDefaultBoundaryBox(NetworkInspector.scenario.getNetwork());
		
//		double factor = 0.9;
//		this.bbox.setCustomBoundaryBox(box.getXMin()+((box.getXMax()-box.getXMin())*(1-factor)),
//				box.getYMin()+((box.getYMax()-box.getYMin())*(1-factor)),
//				box.getXMax()-((box.getXMax()-box.getXMin())*(1-factor)),
//				box.getYMax()-((box.getYMax()-box.getYMin())*(1-factor)));
		
		
		//TODO ersetzen durch etwas sinnvolleres...
		ShapeFileReader reader = new ShapeFileReader();
		Collection<SimpleFeature> features = reader.readFileAndInitialize("C:/Users/Daniel/Dropbox/bsc/input/berlin.shp");
		
		Geometry g = ((Geometry)features.iterator().next().getDefaultGeometry());
		
		this.area = g.buffer(-10.);
//		this.area = g.convexHull().buffer(-10.);
		
	}
	
	private boolean isRoutable(){
		
		//aus klasse "NetworkCleaner"
		final Map<Id,Node> visitedNodes = new TreeMap<Id,Node>();
		Map<Id,Node> biggestCluster = new TreeMap<Id,Node>();
		
		Map<Id,Node> smallClusterNodes = new TreeMap<Id,Node>();
		Map<Id,Link> smallClusterLinks = new TreeMap<Id,Link>();
		
		logger.info("  checking " + NetworkInspector.scenario.getNetwork().getNodes().size() + " nodes and " +
				NetworkInspector.scenario.getNetwork().getLinks().size() + " links for dead-ends...");
		
		boolean stillSearching = true;
		Iterator<? extends Node> iter = NetworkInspector.scenario.getNetwork().getNodes().values().iterator();
		while (iter.hasNext() && stillSearching) {
			Node startNode = iter.next();
			if (!visitedNodes.containsKey(startNode.getId())) {
				Map<Id, Node> cluster = this.findCluster(startNode, NetworkInspector.scenario.getNetwork());
				visitedNodes.putAll(cluster);
				if (cluster.size() > biggestCluster.size()) {
					biggestCluster = cluster;
				} else {
					smallClusterNodes.putAll(cluster);
				}
				if(biggestCluster.size()==NetworkInspector.scenario.getNetwork().getNodes().size()){
					logger.info("size of the biggest cluster equals network size...");
					logger.info("network is routable.");
					return true;
				}
			}
		}
		
		//eigener teil		
		logger.warn("size of the biggest cluster is " + biggestCluster.size() +
				" but network contains " + NetworkInspector.scenario.getNetwork().getNodes().size() + " nodes...");
		logger.warn("network is not routable");
		
		for(Id nodeId : smallClusterNodes.keySet()){
			
//			if()
			
		}
		
//		for(Node node : biggestCluster.values()){
//			smallClusters.removeNode(node.getId());
//		}
		
//		new NetworkWriter(smallClusters).write("C:/Users/Daniel/Dropbox/bsc/input/smallClusters.xml");
		
//		logger.info("small clusters of " + smallClusters.getNodes().size() + " nodes and " + smallClusters.getLinks().size() +
//				" links written to file");
		
		return false;
		
	}
	
	//aus klasse NetworkCleaner
	private Map<Id, Node> findCluster(final Node startNode, final Network network) {

		final Map<Node, DoubleFlagRole> nodeRoles = new HashMap<Node, DoubleFlagRole>(network.getNodes().size());

		ArrayList<Node> pendingForward = new ArrayList<Node>();
		ArrayList<Node> pendingBackward = new ArrayList<Node>();

		TreeMap<Id, Node> clusterNodes = new TreeMap<Id, Node>();
		clusterNodes.put(startNode.getId(), startNode);
		DoubleFlagRole r = getDoubleFlag(startNode, nodeRoles);
		r.forwardFlag = true;
		r.backwardFlag = true;

		pendingForward.add(startNode);
		pendingBackward.add(startNode);

		while (pendingForward.size() > 0) {
			int idx = pendingForward.size() - 1;
			Node currNode = pendingForward.remove(idx);
			for (Link link : currNode.getOutLinks().values()) {
				Node node = link.getToNode();
				r = getDoubleFlag(node, nodeRoles);
				if (!r.forwardFlag) {
					r.forwardFlag = true;
					pendingForward.add(node);
				}
			}
		}

		while (pendingBackward.size() > 0) {
			int idx = pendingBackward.size()-1;
			Node currNode = pendingBackward.remove(idx);
			for (Link link : currNode.getInLinks().values()) {
				Node node = link.getFromNode();
				r = getDoubleFlag(node, nodeRoles);
				if (!r.backwardFlag) {
					r.backwardFlag = true;
					pendingBackward.add(node);
					if (r.forwardFlag) {
						clusterNodes.put(node.getId(), node);
					}
				}
			}
		}

		return clusterNodes;
	}
	
	//eigene methode
	private void checkLinkAttributes() {
		
		logger.info("checking link attributes...");
		
		int writerIndex = 0;
		
		for(Link link : NetworkInspector.scenario.getNetwork().getLinks().values()){
			double distance = 
					Math.sqrt(Math.pow(link.getToNode().getCoord().getX() -
							link.getFromNode().getCoord().getX(),2) +
							Math.pow(link.getToNode().getCoord().getY() -
							link.getFromNode().getCoord().getY(), 2));
			
			int numberOfLanes = (int) link.getNumberOfLanes();
			this.nLanes[numberOfLanes-1]++;
			
			if(link.getCapacity()<500)
				this.linkCapacities[0]++;
			else if(link.getCapacity()<1000&&link.getCapacity()>=500)
				this.linkCapacities[1]++;
			else if(link.getCapacity()<1500&&link.getCapacity()>=1000)
				this.linkCapacities[2]++;
			else if(link.getCapacity()<2000&&link.getCapacity()>=1500)
				this.linkCapacities[3]++;
			else if(link.getCapacity()<2500&&link.getCapacity()>=2000)
				this.linkCapacities[4]++;
			else if(link.getCapacity()>=2500)
				this.linkCapacities[5]++;
			
			if(link.getLength()<7){
				this.lengths[0]++;
				this.lengthBelowStorageCapacity.add(link);
				writerIndex++;
			}
			else if(link.getLength()<15&&link.getLength()>=7)
				this.lengths[1]++;
			else if(link.getLength()<50&&link.getLength()>=15)
				this.lengths[2]++;
			else if(link.getLength()<100&&link.getLength()>=50)
				this.lengths[3]++;
			else if(link.getLength()>=100&&link.getLength()<150)
				this.lengths[4]++;
			else if(link.getLength()>=150&&link.getLength()<200)
				this.lengths[5]++;
			else if(link.getLength()>=200&&link.getLength()<300)
				this.lengths[6]++;
			else if(link.getLength()>=300&&link.getLength()<500)
				this.lengths[7]++;
			else if(link.getLength()>=500)
				this.lengths[8]++;
			
			if(distance<7)
				this.geoLengths[0]++;
			else if(distance<15&&distance>=7)
				this.geoLengths[1]++;
			else if(distance<50&&distance>=15)
				this.geoLengths[2]++;
			else if(distance<100&&distance>=50)
				this.geoLengths[3]++;
			else if(distance>=100&&distance<150)
				this.geoLengths[4]++;
			else if(distance>=150&&distance<200)
				this.geoLengths[5]++;
			else if(distance>=200&&distance<300)
				this.geoLengths[6]++;
			else if(distance>=300&&distance<500)
				this.geoLengths[7]++;
			else if(distance>=500)
				this.geoLengths[8]++;
			
			this.distances.put(link.getId(), distance);
			
			this.totalLength += link.getLength();
			this.totalGLength += distance;
			
			if(this.nodeAttributesChecked){
				if(this.redundantNodes.contains(link.getFromNode())){
					double capacity = link.getCapacity();
					double prevCapacity = link.getFromNode().getInLinks().values().iterator().next().getCapacity();
					if(capacity!=prevCapacity)
						System.out.println("capacity of link" + link.getFromNode().getInLinks().values().iterator().next().getId() + " changes after a redundant node. may be wrong...");
				} else if(this.redundantNodes.contains(link.getToNode())){
					double capacity = link.getCapacity();
					double nextCapacity = link.getToNode().getOutLinks().values().iterator().next().getCapacity();
					if(capacity!=nextCapacity)
						System.out.println("capacity of link" + link.getId() + " changes after a redundant node. may be wrong...");
				}
			}
			
		}
		
		
		logger.info("done.");
		
		logger.info("writing link statistics files...");
		
		File file = new File(this.outputFolder+"/test/lengthBelowStorageCapacity.txt");
		FileWriter writer;
		
		try {
			
			writer = new FileWriter(file);
			for(Link link : this.lengthBelowStorageCapacity){
				writer.write("length of link " + link.getId() + " below min length for storage capacity of one vehicle (" +
						link.getLength() + "m instead of 7 m)\n");
			}
			writer.close();
			
			createLaneStatisticsFiles();
			
			createLinkLengthComparisonFile();
			
			createLinkCapacityFiles();
			
			createLinkLengthFiles();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(writerIndex>0)
			logger.warn(writerIndex + " warnings about storage capacity. written to file " + file.getName());
		
		logger.info("total length of network: " + this.totalLength + "m, total geom. length: " + this.totalGLength + "m");
	
		logger.info("done.");

	}
	
	//eigene methode
	private void checkNodeAttributes() {
		
		logger.info("checking node attributes...");
		
		for(Node node : NetworkInspector.scenario.getNetwork().getNodes().values()){
			
			if(node.getInLinks().size()>0&&node.getOutLinks().size()>0){
			
				this.inDegrees[node.getInLinks().size()-1]++;
				this.outDegrees[node.getOutLinks().size()-1]++;
				
				if(node.getInLinks().size()==1&&node.getOutLinks().size()==1){
				
					Link inLink = node.getInLinks().values().iterator().next();
					Link outLink = node.getOutLinks().values().iterator().next();
				
					if(inLink.getFromNode().equals(outLink.getToNode())){
						if(this.area.contains(new Point(new CoordinateArraySequence(new Coordinate[]{new Coordinate(node.getCoord().getX(),node.getCoord().getY())}),new GeometryFactory())))
							this.deadEndNodes.add(node);
						else
							this.exitRoadNodes.add(node);
					} else{
						if(inLink.getCapacity()==outLink.getCapacity()&&
								inLink.getFreespeed()==outLink.getFreespeed()&&
								inLink.getNumberOfLanes()==outLink.getNumberOfLanes()&&
								inLink.getAllowedModes()==outLink.getAllowedModes())
							this.redundantNodes.add(node);
					}
				}
			}
			
		}
		
		logger.info("done.");
		
		logger.info(this.deadEndNodes.size() + " dead end nodes, "
				+ this.exitRoadNodes.size() + " exit road nodes and " + this.redundantNodes.size() + " redundant nodes found.");
		
		logger.info("writing node statistics files...");
		
		try {
			createNodeDegreesFiles(this.inDegrees, this.outDegrees);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.nodeAttributesChecked = true;
		
		logger.info("done.");
		
	}
	
	//eigene methode
	private void populationMapping(){
		
		//wie wird die population aufs netz "gepackt"?
		
		logger.info("checking where the population is mapped on the network...");
		
		for(Person p : NetworkInspector.scenario.getPopulation().getPersons().values()){
			
			Iterator<PlanElement> planElementIterator = p.getSelectedPlan().getPlanElements().iterator();
			
			while(planElementIterator.hasNext()){
				
				PlanElement pe = planElementIterator.next();
				
				if(pe instanceof Activity){
					
					Coord fromCoord = ((Activity)pe).getCoord();
					
					Id toLinkId = ((NetworkImpl)NetworkInspector.scenario.getNetwork()).getNearestLink(fromCoord).getId();
					
					if(!this.mappedPopulation.containsKey(toLinkId))
						this.mappedPopulation.put(toLinkId, new ArrayList<Activity>());
					
					this.mappedPopulation.get(toLinkId).add((Activity)pe);
					
				}
				
			}
			
		}
		
		logger.info("writing pointers from activities to nearest links...");
		
		CoordinateReferenceSystem crs = MGC.getCRS("DHDN_GK4");
		SimpleFeatureTypeBuilder typeBuilder = new SimpleFeatureTypeBuilder();
		typeBuilder.setName("shape");
		typeBuilder.setCRS(crs);
		typeBuilder.add("pointer",LineString.class);
		typeBuilder.add("ID",String.class);
		typeBuilder.add("LAENGE",Double.class);
		this.builder = new SimpleFeatureBuilder(typeBuilder.buildFeatureType());
		
		ArrayList<SimpleFeature> features = new ArrayList<SimpleFeature>();
		
		Iterator<Id> keyIterator = this.mappedPopulation.keySet().iterator();
		
		GeometryFactory gf = new GeometryFactory();
		
		while(keyIterator.hasNext()){
			
			Id linkId = keyIterator.next();
			Link link = NetworkInspector.scenario.getNetwork().getLinks().get(linkId);
			
			ArrayList<Activity> activities = this.mappedPopulation.get(linkId);
			
			int i = 0;
			
			for(Activity act : activities){
				
				double length = NetworkUtil.getEuclidianDistance(act.getCoord(), link.getCoord());
				
				SimpleFeature feature = this.builder.buildFeature(null, new Object[]{
						gf.createLineString(new Coordinate[]{
								new Coordinate(act.getCoord().getX(),act.getCoord().getY()),
								new Coordinate(link.getCoord().getX(),link.getCoord().getY())
						}),
						new IdImpl(linkId+"_"+Integer.toString(i)).toString(),
						length
				});
				
				features.add(feature);
				i++;
				
			}
			
		}
		
		ShapeFileWriter.writeGeometries(features, "C:/Users/Daniel/Dropbox/bsc/output/mapping/pointers.shp");
		
		logger.info("...done.");
		
	}
	
	//eigene methode
	private void createLinkLengthFiles() throws IOException {
		
		File file = new File(this.outputFolder+"/test/lengthStatistics.txt");
		FileWriter writer = new FileWriter(file);
		
		writer.write("\t\t<7\t<15\t<50\t<100\t<150\t<200\t<300\t<500\t>500\nnObjects");
		
		for(int j=0;j<this.lengths.length;j++){
			writer.write("\t"+this.lengths[j]);
		}
		writer.close();
		
		String[] categories = new String[9];
		categories[0]="< 7";
		categories[1]="< 15";
		categories[2]="< 50";
		categories[3]="< 100";
		categories[4]="< 150";
		categories[5]="< 200";
		categories[6]="< 300";
		categories[7]="< 500";
		categories[8]="> 500";
		
		BarChart chart = new BarChart("Link lengths", "length [m]", "number of objects",categories);
		chart.addSeries("link lengths", this.lengths);
		chart.addSeries("geometric link lengths", this.geoLengths);
		chart.saveAsPng(this.outputFolder+"/test/lengthStatistics.png", 800, 600);
		
	}
	
	//eigene methode
	private void createLaneStatisticsFiles() throws IOException {
		
		File file = new File(this.outputFolder+"/test/laneStatistics.txt");
		FileWriter writer = new FileWriter(file);
		writer.write("NLanes");
		for(int i=0;i<this.nLanes.length;i++){
			writer.write("\t"+ (i+1));
		}
		writer.write("\nnObjects");
		for(int j=0;j<this.nLanes.length;j++){
			writer.write("\t"+this.nLanes[j]);
		}
		writer.close();
		
		BarChart chart = new BarChart("Number of lanes", "number of lanes", "number of objects");
		chart.addSeries("nlanes", this.nLanes);
		chart.saveAsPng(this.outputFolder+"/test/laneStatistics.png", 800, 600);
	}

	//eigene methode
	private void createLinkLengthComparisonFile() throws IOException {
		
		File file = new File(this.outputFolder+"/test/linkLengthComparison.txt");
		FileWriter writer = new FileWriter(file);
		writer.write("Id\tlength\tgeometric Length");
		
		for(Id id : this.distances.keySet()){
			writer.write("\n"+id.toString()+"\t"+
					NetworkInspector.scenario.getNetwork().getLinks().get(id).getLength()+
					"\t"+this.distances.get(id));
		}
		
		writer.close();

	}
	
	//eigene methode
	private void createLinkCapacityFiles() throws IOException{
		
		//kategorien: 500|1000|1500|2000|>...
		String[] categories = new String[6];
		categories[0]="<500";
		categories[1]="<1000";
		categories[2]="<1500";
		categories[3]="<2000";
		categories[4]="<2500";
		categories[5]=">2500";
		
		BarChart chart = new BarChart("Link capacities","capacity","number of objects",categories);
		chart.addSeries("capacities", this.linkCapacities);
		chart.saveAsPng(this.outputFolder+"/test/linkCapacities.png", 800, 600);
		
	}

	//eigene methode
	private void createNodeDegreesFiles(double[] inDegrees, double[] outDegrees) throws IOException {
		
		File file = new File(this.outputFolder+"/test/nodeDegrees.txt");
		FileWriter writer = new FileWriter(file);
		writer.write("in/out\t1\t2\t3\t4\t5\t6\t7\nnobjects");
		
		for(int i=0;i<inDegrees.length;i++){
			writer.write("\t"+inDegrees[i]+"/"+outDegrees[i]);
		}
		writer.close();

		BarChart chart = new BarChart("Node degrees", "degree", "number of objects");
		chart.addSeries("in-degrees", inDegrees);
		chart.addSeries("out-degrees", outDegrees);
		chart.saveAsPng(this.outputFolder+"/test/nodeDegrees.png", 800, 600);
	}
	
	public void shpExportNodeStatistics(Collection<Node> collection){
		initFeatureType();
		Collection<SimpleFeature> features = createFeatures(collection);
		ShapeFileWriter.writeGeometries(features, "C:/Users/Daniel/Dropbox/bsc/pres/exitRoadNodes_osm2.shp");
		
	}
	
	private Collection<SimpleFeature> createFeatures(Collection<Node> collection) {
		List<SimpleFeature> features = new ArrayList<SimpleFeature>();
		for(Node node : collection){
			features.add(getFeature(node));
		}
		return features;
	}

	private SimpleFeature getFeature(final Node node) {
		Point p = MGC.coord2Point(node.getCoord());
		try {
			return this.builder.buildFeature(null, new Object[]{p,node.getId().toString()});
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		}
	}

	private void initFeatureType() {
		CoordinateReferenceSystem crs = MGC.getCRS("DHDN_GK4");
		SimpleFeatureTypeBuilder typeBuilder = new SimpleFeatureTypeBuilder();
		typeBuilder.setName("node");
		typeBuilder.setCRS(crs);
		typeBuilder.add("location",Point.class);
		typeBuilder.add("ID",String.class);
//		typeBuilder.add("in-degree",String.class);
//		typeBuilder.add("out-degree",String.class);
		this.builder = new SimpleFeatureBuilder(typeBuilder.buildFeatureType());
		
	}
	
	private static DoubleFlagRole getDoubleFlag(final Node n, final Map<Node, DoubleFlagRole> nodeRoles) {
		DoubleFlagRole r = nodeRoles.get(n);
		if (null == r) {
			r = new DoubleFlagRole();
			nodeRoles.put(n, r);
		}
		return r;
	}
	
	public Geometry getArea() {
		return area;
	}

	public void setArea(Geometry area) {
		this.area = area;
	}

	static class DoubleFlagRole {
		protected boolean forwardFlag = false;
		protected boolean backwardFlag = false;
	}
	
}
