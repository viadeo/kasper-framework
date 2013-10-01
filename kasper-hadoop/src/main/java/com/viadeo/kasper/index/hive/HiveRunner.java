package com.viadeo.kasper.index.hive;

import com.google.common.collect.Lists;
import org.apache.hadoop.hive.service.HiveInterface;

import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/*
 * Helper classin order to send a script file to Hive
 */
public class HiveRunner {

    private static List<String> lastResults;
    private static List<String> results;

    // ------------------------------------------------------------------------

    public static interface HiveClient {

        void clean() throws Exception;

        void execute(String command) throws Exception;

        List<String> fetchAll() throws Exception;

    }

    // -----

    public static final class HiveInterfaceClient implements HiveClient {

        private final HiveInterface hi;

        public HiveInterfaceClient(final HiveInterface hi) {
            this.hi = checkNotNull(hi);
        }

        @Override
        public void clean() throws Exception {
            hi.clean();
        }

        @Override
        public void execute(final String command) throws Exception {
            hi.execute(command);
        }

        @Override
        public List<String> fetchAll() throws Exception {
            return hi.fetchAll();
        }
    }

    // -----

    public static final class HiveJdbcClient implements HiveClient {

        private final Connection con;
        private ResultSet lastResult;

        public HiveJdbcClient(final Connection con) {
            this.con = checkNotNull(con);
        }

        @Override
        public void clean() throws Exception {
            this.con.close();
        }

        @Override
        public void execute(String command) throws Exception {
            this.lastResult = null;
            final Statement st = this.con.createStatement();
            st.execute(command);
            this.lastResult = st.getResultSet();
        }

        /*
         * FIXME: Naive implementation, a better one will perhaps be required later..
         */
        @Override
        public List<String> fetchAll() throws Exception {
            final List<String> resList = Lists.newArrayList();
            if (null != this.lastResult) {
                while (this.lastResult.next()) {
                    final StringBuffer sb = new StringBuffer();
                    final int nbCols = this.lastResult.getMetaData().getColumnCount();
                    for (int i = 0; i < nbCols; i++) {
                        sb.append(this.lastResult.getObject(i).toString()).append(';');
                    }
                    resList.add(sb.toString());
                }
                this.lastResult.close();
            }
            return resList;
        }
    }

    // ------------------------------------------------------------------------

    public static void runScript(final HiveInterface hive, final File file) throws Exception {
        runScript(new HiveInterfaceClient(hive), file);
    }

    public static void runScript(final HiveInterface hive, final InputStream stream) throws Exception {
        runScript(new HiveInterfaceClient(hive), stream);
    }

    public static void runScript(final Connection con, final File file) throws Exception {
        runScript(new HiveJdbcClient(con), file);
    }

    public static void runScript(final Connection con, final InputStream stream) throws Exception {
        runScript(new HiveJdbcClient(con), stream);
    }

    // ------------------------------------------------------------------------

    public static void runScript(final HiveClient hive, final File file) throws Exception {
        final InputStream stream = new FileInputStream(file);
        runScript(hive, stream);
    }

    public static void runScript(final HiveClient hive, final InputStream stream) throws Exception {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

        results = Lists.newArrayList();

        String line;
        try {
            StringBuffer command = new StringBuffer();
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.startsWith("--")) { /* ignore comment */
                    final String[] tokens = line.split("(?<=;)");
                    for (int i = 0; i < tokens.length; i++) {
                        final String token = tokens[i].trim();

                        //if (token.contentEquals(";")) {
                        //    continue;
                        //}

                        if (!hasText(token)) { /* skip empty lines */
                            continue;
                        }

                        command.append(token).append(" ");

                        if (! token.endsWith(";")) {
                            continue;
                        }

                        final String cmd = command.toString().trim().replaceAll(";$", "");
                        if (!cmd.isEmpty()) {
                            runCommand(hive, cmd);
                        }
                        command = new StringBuffer();
                    }
                }
            }
        } catch (final Exception ex) {
            ex.printStackTrace();
        } finally {
            reader.close();
            try {
                hive.clean();
            } catch (final Exception exc) {
                // Ignore
            }
        }
    }

    // ------------------------------------------------------------------------

    private static void runCommand(final HiveClient hive, final String command) throws Exception {
        hive.execute(command);
        lastResults = hive.fetchAll();
        if (null != lastResults) {
            results.addAll(lastResults);
        }
    }

    // ------------------------------------------------------------------------

    private static boolean hasText(final String str) {
        int strLen;
        if ((null == str) || (0 == (strLen = str.length()))) {
            return false;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    // ------------------------------------------------------------------------

    public static List<String> getLastResults() {
        return lastResults;
    }

    public static List<String> getAllResults() {
        return results;
    }

}
