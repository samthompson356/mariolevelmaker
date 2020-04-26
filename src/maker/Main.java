package maker;

import java.io.IOException;
import java.util.ResourceBundle;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

public class Main {

  public static void main(String[] args)
      throws IOException, ParserConfigurationException, TransformerException {
    ResourceBundle bundle = ResourceBundle.getBundle(LevelMaker.RESOURCE_LOCATION);
    LevelMaker maker = new LevelMaker();
    maker.makeLevel(bundle.getString("LevelPicName"), bundle.getString("ID"), bundle.getString("NextLevelID"));
  }
}
