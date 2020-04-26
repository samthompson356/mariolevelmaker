package maker;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ResourceBundle;
import java.util.Scanner;
import javax.imageio.ImageIO;
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

public class LevelMaker {

  public static final double BRICK_WIDTH = 100.0;
  public static final String LEVEL_TAG = "Level";
  public static final String FILE_OUTPUT_LOCATION = "src/levelFiles/";
  public static final String SOURCE_LOCATION = "src/data/";
  public static final String ID_LABEL = "ID";
  public static final String NEXT_LEVEL_LABEL = "NextLevel";
  public static final String RESOURCE_LOCATION = "maker/resources/levelinfo";
  public static final ResourceBundle levelInfo = ResourceBundle.getBundle(RESOURCE_LOCATION);

  private Document myDoc;

  public LevelMaker() throws ParserConfigurationException {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    myDoc = builder.newDocument();
  }

  public void makeLevel(String fileName, String ID, String NextLevelID) throws IOException, ParserConfigurationException, TransformerException {
    Element level = initializeLevelNode();
    addLevelHeaderInfo(levelInfo.getString("ID"),levelInfo.getString("NextLevelID"),level);
    Element entityList = addElementChild("ImageEntityInstances","",level);
    File f = new File(SOURCE_LOCATION + fileName);
    final BufferedImage image = ImageIO.read(f);
    for (int y = 0; y < image.getHeight(); y ++) {
      for (int x = 0; x < image.getWidth(); x ++) {
        processPixel(image.getRGB(x,y), x, y, entityList,
            Double.parseDouble(levelInfo.getString("PixelSize")));
      }
    }
    writeLevelXML();
  }

  private void addLevelHeaderInfo(String id, String nextLevelID, Element level) {
    System.out.println(levelInfo.getString("ID"));
    addElementChild(ID_LABEL, id, level);
    addElementChild(NEXT_LEVEL_LABEL,nextLevelID,level);
  }

  private Element addElementChild(String label, String content, Element parent) {
    Element childElement = myDoc.createElement(label);
    childElement.setTextContent(content);
    parent.appendChild(childElement);
    return childElement;
  }

  private void writeLevelXML() throws TransformerException, IOException {
    TransformerFactory factory = TransformerFactory.newInstance();
    Transformer transformer = factory.newTransformer();
    transformer.setOutputProperty(OutputKeys.INDENT,"yes");
    DOMSource source = new DOMSource(myDoc);
    File output = new File(FILE_OUTPUT_LOCATION + levelInfo.getString("OutputName"));
    StreamResult streamResult = new StreamResult(output);
    transformer.transform(source,streamResult);
    removeFirstLine(output);
    System.out.println("Success creating file.");
  }

  //https://stackoverflow.com/questions/13178397/how-to-remove-first-line-of-a-text-file-in-java
  private void removeFirstLine(File output) throws IOException {
    Scanner fileScanner = new Scanner(output);
    fileScanner.nextLine();
    FileWriter fileStream = new FileWriter(output);
    BufferedWriter out = new BufferedWriter(fileStream);
    while (fileScanner.hasNextLine()) {
      String next = fileScanner.nextLine();
      if (next.equals("\n")) {
        out.newLine();
      }
      else {
        out.write(next);
      }
      out.newLine();
    }
    out.close();
//    RandomAccessFile lineRemover = new RandomAccessFile(output,"rw");
//    int firstLineLength = lineRemover.readLine().length();
//    lineRemover.seek(0);
//    for (int i = 0; i < firstLineLength; i ++) {
//      lineRemover.write(' ');
//    }
//    lineRemover.skipBytes(firstLineLength);
//    lineRemover.close();
  }

  private Element initializeLevelNode() {
    Element root = myDoc.createElement(LEVEL_TAG);
    myDoc.appendChild(root);
    return root;
  }

  private void processPixel(int color, int x, int y, Element entityList, double pixelSize) {
    System.out.printf("%x\n",color);
    System.out.println("Integer.toHexString(color) = " + Integer.toHexString(color));
    if (levelInfo.containsKey(Integer.toHexString(color))) {
      placeEntity(levelInfo.getString(Integer.toHexString(color)),x * pixelSize,y * pixelSize, entityList);
    }
  }

  private void placeEntity(String type, double xPos, double yPos, Element entityList) {
    Element entity = myDoc.createElement("ImageEntityInstance");

    addElementChild("Name",type,entity);
    addElementChild("XPos",String.valueOf(xPos),entity);
    addElementChild("YPos",String.valueOf(yPos),entity);

    entityList.appendChild(entity);
  }
}
