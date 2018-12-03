package model.process;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.*;
import java.util.HashMap;
import java.util.List;


enum  ReqType {
    GET,
    POST,
    PUT,}

public class RemoteSource implements ProcessSource {

    URI url;

    HashMap<Process, Boolean> cachedProcs =  new HashMap<>(100);;


    public RemoteSource(String  ip) throws MalformedURLException,
                                        URISyntaxException {
        String urlStr = "https://" + ip + ":80";
        this.url = new URI(urlStr);
    }

    public RemoteSource(InetAddress  ip) throws MalformedURLException, URISyntaxException {
        String urlStr = "https://" + ip + ":80";
        this.url = new URI(urlStr);
    }

    /// TODO
    private void  updateViaDif(List<Process> newRecv){

    }

    @SuppressWarnings("unchecked")
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

    // TODO concat path
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

    @Override
    public String toString(){
        return "Remote @ " + this.url.getHost();
    }
}
