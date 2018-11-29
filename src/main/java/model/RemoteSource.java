package model;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


enum  ReqType {
    GET,
    POST,
    PUT,}

public class RemoteSource implements ProcessSource {

    URL url;

    HashMap<Process, Boolean> cachedProcs;


    public RemoteSource(String  ip) throws MalformedURLException {
        this.url = new URL(ip);

        this.cachedProcs = new HashMap<>(100);
    }

    /// TODO
    private void  updateViaDif(List<Process> newRecv){

    }

    private void syncRemoteState( )
            throws IOException, ClassNotFoundException {

        HttpURLConnection connection =
                this.getConnection(ReqType.GET, "/sim/get");

        final InputStream inStream =  connection.getInputStream();
        final ObjectInputStream objIn = new ObjectInputStream(inStream);

        List<Process> tempList = (List<Process>) objIn.readObject();

        if (tempList != null){
           // this.updateViaDif(tempList) ; TODO

        } else {
            System.out.println("Received unexpected result: " + tempList);
        }
    }

    private HttpURLConnection getConnection(ReqType reqType, String endPoint   ) throws IOException {
        URL endpoint = new URL(this.url.toString() +  endPoint);

        final HttpURLConnection connection = (HttpURLConnection) endpoint.openConnection();

        if (reqType.equals(ReqType.GET)) connection.setRequestMethod("GET");
        if (reqType.equals(ReqType.POST)) connection.setRequestMethod("POST");
        return connection;
    }

    @Override // Todo
    public List<Process> getAll() {
       return null;
    }

    @Override
    public void kill(int pid) throws IOException {

    }

    @Override
    public Process generateProcess() {
        return null;
    }
}
