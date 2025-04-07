import com.ppdiscover.PPDocument;
import com.ppdiscover.utils.PPExtractor;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PPExtractorTest {

    @Test
    public void testPPExtractor() throws FileNotFoundException {
        PPDocument result = PPExtractor.extractToObject("C:\\Users\\vhole\\code\\PPDiscover\\src\\test\\resources\\powerpoints\\1.s.i fasten(I)_09.03.2025_Houlkær_PP.pptx");

        if (result != null) {
            System.out.println("Numbers: " + result.getSalmeNumre());
            System.out.println("Titler: " + result.getSalmeTitler());

        }

        assertEquals(result.getSalmeNumre().size(), result.getSalmeTitler().size());
    }

    @Test
    public void testFilenameHandling() throws FileNotFoundException {
        PPDocument doc = new PPDocument();
        String filename = "1.s.i fasten(I)_09.03.2025_Houlkær_PP.pptx";
        PPExtractor.updateDocumentWithFileInformation(doc, filename);
    }
}
