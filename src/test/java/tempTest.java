import Converter.Controller.DB.NoSQL.Cassandra.CassandraConnector;
import Converter.ViewModel.NoSQLTypes;
import org.junit.Test;

/**
 * Created by szef on 2014-07-26.
 */
public class tempTest {

    @Test
    public void CassandraConnection() throws Exception {
        CassandraConnector cas;
        CassandraConnector.getClient(NoSQLTypes.Cassandra);
    }
}
