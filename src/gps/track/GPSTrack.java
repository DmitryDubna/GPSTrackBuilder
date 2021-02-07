package gps.track;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class GPSTrack {
	
	private final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
	
	private String deviceName;
	private String trackName;
	private List<GPSTrackPoint> points;
	private static Random rand = new Random();
	
	
	public String getTrackName() 
	{
		return trackName;
	}
	
	public void setTrackName(String trackName) 
	{
		this.trackName = trackName;
	}


	public List<GPSTrackPoint> getPoints() 
	{
		return points;
	}


	// constructor
	public GPSTrack(String sourceFileName, String deviceName, String trackName)
	{		
		this(deviceName, trackName);
		// parse source .gpx-file
		parseGpx(sourceFileName, points);
	}
	
	
	public GPSTrack(String deviceName, String trackName)
	{
		this.deviceName = deviceName;
		this.trackName = trackName;
		// create track point list
		points = new ArrayList<GPSTrackPoint>();
	}
	
	
	public GPSTrack(String deviceName, String trackName, List<GPSTrackPoint> points)
	{
		this.deviceName = deviceName;
		this.trackName = trackName;
		this.points = points;
	}
	
	
	// clone track
	public GPSTrack clone()
	{
		GPSTrack track = new GPSTrack(this.deviceName, this.trackName);
		track.points.addAll(points);
		return track;
	}
	
	
	// parse input .gpx-file
	private void parseGpx(String sourceFileName, List<GPSTrackPoint> points)
	{
		try 
		{
			// get factory
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			// get document builder
			DocumentBuilder builder = factory.newDocumentBuilder();
			// get document
			Document doc = builder.parse(new File(sourceFileName));
			// normalize xml structure
			doc.getDocumentElement().normalize();
			// get <gpx> (root) element
			Element gpxElement = doc.getDocumentElement();
			// get <trkpt> node list
			NodeList trkptNodes = gpxElement.getElementsByTagName("trkpt");
			// iterate each <trkpt> node and create corresponding GPSTrackPoint
			// and add it to list
			for (int i = 0; i < trkptNodes.getLength(); i++)
			{
				try
				{
					// get node
					Node trkptNode = trkptNodes.item(i);
				 	GPSTrackPoint point = makeTrackPointFromNode(trkptNode);
				 	// add point to list
					if (point != null)
						points.add(point);
				} 
				catch (ParseException e)
				{
					e.printStackTrace();
				}				
			}
		} 
		catch (ParserConfigurationException | SAXException | IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	
	// make GPS track point from node
	private GPSTrackPoint makeTrackPointFromNode(Node node) throws ParseException
	{
		if (node.getNodeType() != Node.ELEMENT_NODE)
			return null;
		
		Element e = (Element)node;
		// latitude
		double lat = Double.parseDouble(e.getAttribute("lat"));
		// longitude
		double lon = Double.parseDouble(e.getAttribute("lon"));
		// height (<ele>)
		double height = (e.hasAttribute("ele") ? 
							Double.parseDouble(e.getAttribute("ele")) : 0.0);
		// UTC date and time (<time>)
		Date date = null;
		// get height value
	 	NodeList heightValues = e.getElementsByTagName("ele");
	 	if ((heightValues != null) && (heightValues.getLength() > 0))
	 		height = Double.parseDouble(heightValues.item(0).getTextContent());
	 	// get date value
	 	NodeList timeValues = e.getElementsByTagName("time");
	 	if ((timeValues != null) && (timeValues.getLength() > 0))
	 	{
	 		String timeStamp = timeValues.item(0).getTextContent();
	 		date = convertStringToDate(timeStamp, DATE_FORMAT);
	 	}
	 	// create GPSTrackPoint
	 	return new GPSTrackPoint(lat, lon, height, date);
	}
	
	
	public GPSTrack makeTrackWithDeviation(Date fromDate, Date toDate,
			double deltaCoord, double newHeight, double deltaHeight)
	{
		List<GPSTrackPoint> newPoints = 
				createTrackPointsWithDeviation(getPoints(), fromDate, toDate, deltaCoord, newHeight, deltaHeight);
		return new GPSTrack(deviceName, trackName, newPoints);
	}
	
	// return list of track points with new parameters:
	// change <lat> and <lon>, set <ele> and <time> of source points
	public static List<GPSTrackPoint> createTrackPointsWithDeviation(List<GPSTrackPoint> srcPoints,
				Date fromDate, Date toDate, double deltaCoord, double newHeight, double deltaHeight)
	{
		// track must contain not less then 2 points
		if (srcPoints.size() < 2)
			return null;
		
		long secondsBetweenDates = (toDate.getTime() - fromDate.getTime()) / 1000;
		long secondsBetweenPoints = secondsBetweenDates / (srcPoints.size() - 1);
		int deltaSec = (int) (secondsBetweenPoints / 3);
		// result point list
		List<GPSTrackPoint> resPoints = new ArrayList<GPSTrackPoint>();
		// add all points with changed coordinates and date to new list
		for(int i = 0; i < srcPoints.size(); i++)
		{
			// get current point
			GPSTrackPoint p = srcPoints.get(i);
			// correct latitude and longitude
			double changedLat = getRandomShiftedValue(p.getLatitude(), deltaCoord);
			double changedLon = getRandomShiftedValue(p.getLongitude(), deltaCoord);
			double changedHeight = getRandomShiftedValue(newHeight, deltaHeight);
			// calculate date
			Date changedDate = null;
			Calendar calendar = Calendar.getInstance();
			if (i == 0)							// first point has date fromDate
				changedDate = fromDate;
			else if (i == srcPoints.size() - 1)	// last point has date toDate
				changedDate = toDate;
			else
				// generate shifted date
				changedDate = generateShiftedDate(fromDate, (int)(i*secondsBetweenPoints), deltaSec);
			// shift date (UTC+3)
			calendar.setTime(changedDate);
			calendar.add(Calendar.HOUR, -3);
			changedDate = calendar.getTime();
			// create point with new parameters
			GPSTrackPoint newPoint = new GPSTrackPoint(changedLat, changedLon,
														changedHeight, changedDate);
			// add new point to result list
			resPoints.add(newPoint);
		}
		return resPoints;
	}
	
	
	public void shiftTrackDate(Date fromDate)
	{
		// check if track is not empty
		if (points.isEmpty())
			return;
		// get first track point time (msec)
		long firstPointTime = points.get(0).getTime();
		// calculate difference
		long timeShift = fromDate.getTime() - firstPointTime;
		// shift date for each point
		for (GPSTrackPoint point : points)
		{
			// convert date to instant and increment
			Instant instant = point.getDate().toInstant().plusMillis(timeShift);
			// set new date to point
			point.setDate(Date.from(instant));
		}
	}
	
	
	public void changeDuration(Date fromDate, Date toDate)
	{
		// check if track is not empty
		if (points.isEmpty() || (points.size() < 2))
			return;
		// new duration
		long newDuration = toDate.getTime() - fromDate.getTime();		
		// old track start point
		long oldStartTime = points.get(0).getTime();
		// old track duration
		long oldDuration = points.get(points.size() - 1).getTime() - oldStartTime;
		for (GPSTrackPoint point : points)
		{
			// current segment relative duration (old)
			long oldSegmentDuration = (point.getTime() - oldStartTime);
			// current segment relative factor (old)
			double segmentRelativeFactor = (double)oldSegmentDuration / oldDuration;
			// new segment duration
			long newSegmentDuration = (long) (segmentRelativeFactor * newDuration);
			// convert date to instant and increment
			Instant instant = fromDate.toInstant().plusMillis(newSegmentDuration);
			// set new date to point
			point.setDate(Date.from(instant));
		}
	}
	
	
	public void shiftCoordinates(double deltaCoord)
	{
		// change latitude and longitude for each point 
		for(int i = 0; i < points.size(); i++)
		{
			// get current point
			GPSTrackPoint p = points.get(i);
			// correct latitude and longitude
			double changedLat = getRandomShiftedValue(p.getLatitude(), deltaCoord);
			double changedLon = getRandomShiftedValue(p.getLongitude(), deltaCoord);
			// set new latitude and longitude
			p.setLatitude(changedLat);
			p.setLongitude(changedLon);
		}
	}
	
	
	// return value with random shift
	private static double getRandomShiftedValue(double value, double deltaShift)
	{
		double sign = (rand.nextDouble() < 0.5) ? 1 : -1;
		double shift = rand.nextDouble() * deltaShift;
		return value + shift * sign;
	}
	
	
	// generate shifted date
	private static Date generateShiftedDate(Date initDate, int offsetSec, int deltaSec)
	{
		Calendar calendar = Calendar.getInstance();
		// set calendar initial date
		calendar.setTime(initDate);
		// calculate seconds shift
		int sign = (rand.nextDouble() < 0.5) ? 1 : -1;
		int shift = (deltaSec == 0 ? 0 : rand.nextInt(deltaSec) * sign);
		// add amount of seconds corresponding to this point + shift
		calendar.add(Calendar.SECOND, offsetSec + shift);
		return calendar.getTime();
	}
	
	
	// write track points to .gpx-file
	public boolean writeGpxFile(String outFileName/*, List<GPSTrackPoint> points*/)
	{
		boolean success = false;
		try 
		{
			// build doc
			Document doc = buildDocument(points);
			// write document content to .gpx-file
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			// set transformer properties
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(outFileName));
			transformer.transform(source, result);
			// set success ok
			success = true;
		} 
		catch (ParserConfigurationException | TransformerException e) 
		{
			e.printStackTrace();
		} 
		return success;
	}
	
	
	// build document
	private Document buildDocument(List<GPSTrackPoint> points) throws ParserConfigurationException
	{
		// factory and builder
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		// root element (<gpx>)
		Document doc = builder.newDocument();
		Element gpxElement = doc.createElement("gpx");
		// set root element attributes
		setRootAttributes(gpxElement);
		doc.appendChild(gpxElement);
		// -- garmin meta data
		// meta data date = last point date
		Date metaDate = points.get(points.size() - 1).getDate();
		Element metaElement = createGarminMetaData(doc, metaDate);
		gpxElement.appendChild(metaElement);
		// -- track element (<trk>)
		Element trkElement = doc.createElement("trk");
		gpxElement.appendChild(trkElement);
		// ---- name element (<name>)
		Element nameElement = doc.createElement("name");
		// add name string
		nameElement.appendChild(doc.createTextNode(trackName));
		trkElement.appendChild(nameElement);
		// ---- trkseg element (<trkseg>)
		Element trksegElement = doc.createElement("trkseg");
		trkElement.appendChild(trksegElement);
		
		// append all track points
		appendPointsToSegment(doc, trksegElement, points);
		
		return doc;
	}
	
	
	// add all track points to track segment
	private void appendPointsToSegment(Document doc, Element segElement, List<GPSTrackPoint> points)
	{
		for (GPSTrackPoint point : points) 
		{
			// trkpt element (<trkpt>)
			Element trkptElement = doc.createElement("trkpt");
			// set attributes (latitude and longitude)
			trkptElement.setAttribute("lat", String.valueOf(point.getLatitude()));
			trkptElement.setAttribute("lon", String.valueOf(point.getLongitude()));
			segElement.appendChild(trkptElement);
			// -- height element (<ele>)
			Element heightElement = doc.createElement("ele");
			// add height string
			heightElement.appendChild(doc.createTextNode(String.valueOf(point.getHeight())));
			trkptElement.appendChild(heightElement);
			// -- time element (<time>)
			Element timeElement = doc.createElement("time");
			// convert date to string
			String dateString = convertDateToString(point.getDate(), DATE_FORMAT);
	 		// add date string
			timeElement.appendChild(doc.createTextNode(dateString));
			trkptElement.appendChild(timeElement);
		}		
	}
	
	
	// set root element attributes
	private void setRootAttributes(Element root)
	{
		root.setAttribute("xmlns", "http://www.topografix.com/GPX/1/1");
		root.setAttribute("xmlns:gpxx", "http://www.garmin.com/xmlschemas/GpxExtensions/v3");
		root.setAttribute("xmlns:wptx1", "http://www.garmin.com/xmlschemas/WaypointExtension/v1");
		root.setAttribute("xmlns:gpxtpx", "http://www.garmin.com/xmlschemas/TrackPointExtension/v1");
		root.setAttribute("creator", deviceName);
		root.setAttribute("version", "1.1");
		root.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		root.setAttribute("xsi:schemaLocation", "http://www.topografix.com/GPX/1/1 "
										+ "http://www.topografix.com/GPX/1/1/gpx.xsd "
										+ "http://www.garmin.com/xmlschemas/GpxExtensions/v3 "
										+ "http://www8.garmin.com/xmlschemas/GpxExtensionsv3.xsd "
										+ "http://www.garmin.com/xmlschemas/WaypointExtension/v1 "
										+ "http://www8.garmin.com/xmlschemas/WaypointExtensionv1.xsd "
										+ "http://www.garmin.com/xmlschemas/TrackPointExtension/v1 "
										+ "http://www.garmin.com/xmlschemas/TrackPointExtensionv1.xsd");
	}
	
	
	// create meta data element
	public Element createGarminMetaData(Document doc, Date date)
	{
		// meta data element (<metadata>)
		Element metaElement = doc.createElement("metadata");
		// -- link element (<link>)
		Element linkElement = doc.createElement("link");
		linkElement.setAttribute("href", "http://www.garmin.com");
		metaElement.appendChild(linkElement);
		// ---- text element (<text>)
		Element textElement = doc.createElement("text");
		textElement.appendChild(doc.createTextNode("Garmin International"));
		linkElement.appendChild(textElement);
		// -- time element (<time>)
		Element timeElement = doc.createElement("time");
		String dateString = convertDateToString(date, DATE_FORMAT);
		timeElement.appendChild(doc.createTextNode(dateString));
		metaElement.appendChild(timeElement);
		// return meta data element
		return metaElement;
	}
	
	
	// extend point list with additional points
	public GPSTrack makeExtendedTrack(int pointsPerSection)
	{
		List<GPSTrackPoint> extPoints = new ArrayList<GPSTrackPoint>();
		for (int i = 0; i < points.size() - 1; i++)
		{
			// add current point to result
			extPoints.add(points.get(i));
			// add intermediate points to result
			List<GPSTrackPoint> subList = 
					makeIntermediatePoints(points, i, i + 1, pointsPerSection);
			extPoints.addAll(subList);
		}
		// add last point
		extPoints.add(points.get(points.size() - 1));
		// return result
		return new GPSTrack(deviceName, trackName, extPoints);
	}
	
	
	// append num points between beginPos and endPos to list
	private static List<GPSTrackPoint> makeIntermediatePoints(
						List<GPSTrackPoint> list, int beginPos, int endPos, int num)
	{
		// create result list
		List<GPSTrackPoint> result = new ArrayList<GPSTrackPoint>();
		// get source points 
		GPSTrackPoint beginPoint = list.get(beginPos);
		GPSTrackPoint endPoint = list.get(endPos);
		// calculate latitude, longitude and height increments
		double deltaLat = (endPoint.getLatitude() - beginPoint.getLatitude()) / (num + 1);
		double deltaLon = (endPoint.getLongitude() - beginPoint.getLongitude()) / (num + 1);
		double deltaHeight = (endPoint.getHeight() - beginPoint.getHeight()) / (num + 1);
		// calculate time increment
		long secondsBetweenDates = 
				(endPoint.getDate().getTime() - beginPoint.getDate().getTime()) / 1000;
		long secondsBetweenPoints = secondsBetweenDates / (num + 1);
		int deltaSec = (int) (secondsBetweenPoints / 3);
		// start adding position
		for (int i = 1; i <= num; i++)
		{
			// calculate incremented values
			double newLat = beginPoint.getLatitude() + i * deltaLat;
			double newLon = beginPoint.getLongitude() + i * deltaLon;
			double newHeight = beginPoint.getHeight() + i * deltaHeight;
			// generate shifted date
			Date newDate = generateShiftedDate(beginPoint.getDate(), 
												(int)(i*secondsBetweenPoints), deltaSec);
			// append new point
			GPSTrackPoint p = new GPSTrackPoint(newLat, newLon, newHeight, newDate);
			// add to list
			result.add(p);
		}
		return result;
	}
	
	
	// interpolate heights by Bezier spline
	public static void interpolateHeights(List<GPSTrackPoint> points1, List<GPSTrackPoint> points2)
	{
		// indices
		int idx0 = 0;
		int idx1 = (points1.size() - 1) / 5;
		int idx2 = (points1.size() - 1) / 2;
		int idx3 = points1.size() - 1;		
		// points
		GPSTrackPoint p0 = points1.get(idx0).clone();
		GPSTrackPoint p1 = points1.get(idx1).clone();
		GPSTrackPoint p2 = points1.get(idx2).clone();
		GPSTrackPoint p3 = points1.get(idx3).clone();
		// calculate each height 
		// but not for first and last points
		for (int i = 0; i < points1.size(); i++)
		{			
			GPSTrackPoint p = points1.get(i);
			// coefficient
			double t = (double)((p.getTime() - p0.getTime())) / 
						(double)((p3.getTime() - p0.getTime()));
			// height
			double h = Math.pow(1 - t, 3) * p0.getHeight() +
						+ 3 * Math.pow(1 - t, 2) * t * p1.getHeight() +
						+ 3 * (1 - t) * Math.pow(t, 2) * p2.getHeight() + 
						+ Math.pow(t, 3) * p3.getHeight();
			// set heights
			// points 1
			p.setHeight(h);
			// points 2
			points2.get(i).setHeight(h);
		}
	}
	
	
	public static void equateHeights(List<GPSTrackPoint> points1, List<GPSTrackPoint> points2)
	{
		for (int i = 0; i < points1.size(); i++)
		{			
			// get point 1 height
			double height = points1.get(i).getHeight();
			// set point 2 height
			points2.get(i).setHeight(height);
		}
	}
	
	
	// convert String to Date
	public static Date convertStringToDate(String timeString, String pattern) throws ParseException
	{
		DateFormat iso8601 = new SimpleDateFormat(pattern);
		return iso8601.parse(timeString); 		
	}
	
	
	// convert Date to String
	public static String convertDateToString(Date date, String pattern)
	{
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		return format.format(date);
	}
	
}
