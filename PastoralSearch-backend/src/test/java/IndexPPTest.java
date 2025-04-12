import com.ppdiscover.PPDocument;
import com.ppdiscover.utils.SolrUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IndexPPTest {


    @Test
    public void testIndexing() throws SolrServerException, IOException {
        List<Integer> salmeNumre = new ArrayList<>(Arrays.asList(12, 123, 321));
        List<String> salmeTitler = new ArrayList<>(Arrays.asList("title 1", "title 2", "title 3"));
        List<String> salmeTekster = new ArrayList<>(Arrays.asList("indhold 1", "indhold 2", "indhold 3"));

        PPDocument testDoc = new PPDocument();
        testDoc.setContent("Dette er alt postens indhold");
        testDoc.setFileName("/PP/Første_række/1.s.e.trinitatis");
        testDoc.setSalmeNumre(salmeNumre);
        testDoc.setSalmeTitler(salmeTitler);
        testDoc.setSalmeTekster(salmeTekster);

        SolrUtils.indexPowerpointDTO(testDoc);
    }
}
