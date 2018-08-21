package turn.zio.zara.travel_log;

import android.os.Environment;
import android.util.Log;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.w3c.dom.DOMException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


/**
 * Created by 하루마다 on 2017-06-15.
 */

public class CreateKMLFile {

    private static final String TAG = "CreateKMLFile";

    public static String createKML(ArrayList<LocationInfo> locationHistory, String userId) {
        Document doc = null;

        try {

            doc = new Document();

            Element kml = new Element("kml", "http://www.opengis.net/kml/2.2");
            Element document = new Element("Document");
            Element placeMark = new Element("Placemark");
            Element lineString = new Element("LineString");
            Element coordinates = new Element("coordinates");

            Element name = new Element("name");
            Element open = new Element("open");
            Element style = new Element("Style");
            Element lineStyle = new Element("LineStyle");
            Element color = new Element("Color");
            Element width = new Element("width");
//            Element gx = new Element("gx","labelVisibility");  <gx:labelVisibility> 형식으로 구현해야함

            Element markName = new Element("name");
            Element styleUrl = new Element("styleUrl");
            Element extrude = new Element("extrude");
            Element tessellate = new Element("tessellate");

            kml.addContent(document);
            {

                document.addContent(name);
                name.setText("" + userId + " 발자취");
                document.addContent(open);
                open.setText("1");

                style.setAttribute("id", "lineStyle");
                document.addContent(style);
                {
                    style.addContent(lineStyle);
                    {
                        lineStyle.addContent(color);
                        color.setText("7f0000ff");
                        lineStyle.addContent(width);
                        width.setText("4");
//                        lineStyle.addContent(gx); gx.setText("1");
                    }
                }
                document.addContent(placeMark);
                {
                    placeMark.addContent(markName);
                    markName.setText("LineStyle");
                    placeMark.addContent(styleUrl);
                    styleUrl.setText("#lineStyle");
                    placeMark.addContent(lineString);
                    {
                        lineString.addContent(extrude);
                        extrude.setText("1");
                        lineString.addContent(tessellate);
                        tessellate.setText("1");

                        /* 실제 좌표값 입력 */
                        lineString.addContent(coordinates);

                        String location = "";
                        for (LocationInfo info : locationHistory) {
                            location += info.getLongitude() + "," + info.getLatitude() + ",0 \n";
                        }
                        Log.d("location", location);
                        coordinates.setText(location);
                    }
                }
            }

            doc.setRootElement(kml);

        } catch (DOMException e1) {
            e1.printStackTrace();

        }

        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/travelLog/kml/";
        String name = System.currentTimeMillis() + "step_log.kml";
        if (doc == null) return "";
        File file = new File(path);
        if (!file.exists())
            file.mkdirs();
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(path + name);
            //xml 파일을 떨구기 위한 경로와 파일 이름 지정해 주기
            XMLOutputter serializer = new XMLOutputter();

            Format f = serializer.getFormat();
            f.setEncoding("UTF-8");                        /* encoding 타입을 UTF-8 로 설정 */
            f.setIndent(" ");
            f.setLineSeparator("\r\n");
            f.setTextMode(Format.TextMode.TRIM);
            serializer.setFormat(f);

            serializer.output(doc, out);
            out.flush();


//            String 으로 xml 출력
            XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat().setEncoding("UTF-8"));
            Log.d(TAG, outputter.outputString(doc));

        } catch (IOException e) {
            System.err.println(e);

        } finally {

            try {
                if (out != null) out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return name;
    }
}