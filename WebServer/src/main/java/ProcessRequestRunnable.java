import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.io.Resources;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

/**
 * @author Diego Urrutia Astorga <durrutia@ucn.cl>
 * @version 20170330130700
 */
public class ProcessRequestRunnable implements Runnable {

    /**
     * Logger de la clase
     */
    private static final Logger log = LoggerFactory.getLogger(ProcessRequestRunnable.class);

    /**
     * Contenedor de Chat
     */
    private static final List<Mensaje> chats = Lists.newArrayList();

    /**
     * Socket asociado al cliente.
     */
    private Socket socket;

    /**
     * Nick del usuario.
     */
    private static String nickname = "";

    /**
     * Email de usuario.
     */
    private static String email = "";

    private static boolean isLogged = false;

    /**
     * Constructor
     *
     * @param socket
     */
    public ProcessRequestRunnable(final Socket socket) {
        this.socket = socket;
    }


    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {

        // Cronometro ..
        final Stopwatch stopWatch = Stopwatch.createStarted();

        log.debug("Connection from {} in port {}.", socket.getInetAddress(), socket.getPort());

        // A dormir!
        try {
            Thread.sleep(RandomUtils.nextInt(100, 500));
        } catch (InterruptedException e) {
            log.error("Error in sleeping", e);
        }

        try {
            processRequest(this.socket);
        } catch (Exception ex) {
            log.error("Error processing request", ex);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                // Nothing here
            }
        }

        log.debug("Request timex: {}.", stopWatch);

    }

    /**
     * Procesar peticion
     *
     * @param socket
     */
    private static void processRequest(final Socket socket) throws IOException {

        // BufferedReade
        final BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // Peticion
        final ArrayList<String> request = getRequest(in);
        log.debug("Request detected: {}", request);

        // Output
        final OutputStream outputStream = IOUtils.buffer(socket.getOutputStream());

        log.debug("Writing data for: {}", request);

        // Handling request
        final String[] parts = StringUtils.split(request.get(0));
        final String verbo = parts[0];
        final String uri = parts[1];
        final String version = parts[2];

        // Prints content of first line (request)
        log.debug("VERBO: {}", verbo);
        log.debug("URI: {}", uri);
        log.debug("VERSION: {}", version);

        // Deteccion de version
        if (!StringUtils.equals("HTTP/1.1", version)) {

            log.warn("Wrong version: {}", version);

        }

        //Writing response.
        writeHeader(outputStream);

        if(StringUtils.startsWith(uri, "/chat")){

            if(!isLogged){

                final String login = readFile("login.html");
                IOUtils.write(login + "\r\n", outputStream, Charset.defaultCharset());
                isLogged = true;

            }else{

                writeBody(outputStream,request);

            }

        }else{

            final String error = readFile("error.html");
            IOUtils.write(error + "\r\n", outputStream, Charset.defaultCharset());

        }

        // Cierro el stream
        IOUtils.closeQuietly(outputStream);

    }

    /**
     * @param filename
     * @return the contenido del archivo.
     */
    private static String readFile(final String filename) {

        // URL del index
        URL url;
        try {
            url = Resources.getResource(filename);
        } catch (IllegalArgumentException ex) {
            log.error("Can't find file", ex);
            return null;
        }

        // Contenido
        try {
            return IOUtils.toString(url, Charset.defaultCharset());
        } catch (IOException ex) {
            log.error("Error in read", ex);
            return null;
        }

    }

    private static String processPost(BufferedReader in){

        return null;

    }

    /**
     * Obtengo todo el texto del request.
     * @return the request.
     */
    private static ArrayList<String> getRequest(BufferedReader in) {

        ArrayList<String> request = new ArrayList<String>();

        try {

            String line;
            int n = 0;
            int cant = 0;

            line = in.readLine();

            while(!line.equals("")){

                log.debug("Header line {}: {}",++n,line);
                request.add(line);

                final String contentHeader = "Content-Length: ";

                if(line.startsWith(contentHeader)){

                    cant = Integer.parseInt(line.substring(contentHeader.length()));

                }

                line = in.readLine();

            }

            int c = 0;
            String contenido = "";

            for (int i = 0; i < cant; i++) {

                c = in.read();
                contenido += (char)c;
                log.debug("Body line {}: {}", (i+1),((char) c));

            }

            request.add(contenido);

        } catch (Exception e) {

            e.printStackTrace();

        }

        return request;

    }

    /**
     * Escribe el encabezado del protocolo HTTP.
     *
     * @param outputStream
     * @throws IOException
     */
    private static void writeHeader(OutputStream outputStream) throws IOException {

        // Header
        IOUtils.write("HTTP/1.0 200 OK\r\n", outputStream, Charset.defaultCharset());
        IOUtils.write("Content-type: text/html\r\n", outputStream, Charset.defaultCharset());

        // end-header
        IOUtils.write("\r\n", outputStream, Charset.defaultCharset());

    }


    private static void writeBody(final OutputStream outputStream, ArrayList<String> request) throws IOException {

        if(StringUtils.contains(request.get(0),"POST")){

            String parametros = request.get(request.size()-1);

            if(StringUtils.contains(parametros,"inputNick=")
                    && StringUtils.contains(parametros,"&inputMail=")){

                nickname = StringUtils.substringBetween(parametros,"inputNick=","&");
                email = StringUtils.substringAfter(parametros,"&inputMail=");
                email = StringUtils.replace(email,"%40","@");
                email = email.toLowerCase();
                email = email.trim();
                log.debug("Nickname: {}", nickname);
                log.debug("Email: {}", email);

            }else{

                String msg = StringUtils.substringAfter(parametros,"msgText=");
                Date date = Calendar.getInstance().getTime();

                System.out.println("USUARIO: " +nickname);
                System.out.println("EMAIL: " +email);
                System.out.println("MENSAJE: " +msg);
                System.out.println("FECHA: " +date.toString());

                Mensaje mensaje = new Mensaje();
                mensaje.setUsuario(nickname);
                mensaje.setFecha(date);
                mensaje.setMensaje(msg);

                String avatar = "https://www.gravatar.com/avatar/";

                Gravatar gravatar = new Gravatar();
                gravatar.setSize(160);
                gravatar.setRating(GravatarRating.GENERAL_AUDIENCES);
                gravatar.setDefaultImage(GravatarDefaultImage.IDENTICON);
                avatar = gravatar.getUrl(email);

                mensaje.setAvatar(avatar);
                System.out.println("AVATAR: " +avatar);

                log.debug("Msg to include: {}", msg);

                // Sincronizacion
                synchronized (chats) {
                    chats.add(mensaje);
                }

            }

        }

        // Listado completo de chat
        final StringBuffer sb = new StringBuffer();

        // Linea de chat
        final String chatline = readFile("chatline.html");

        // Siempre el mismo comportamiento
        synchronized (chats) {

            for (Mensaje m : chats) {

                String parte1 = StringUtils.replace(chatline,"CONTENT", m.getMensaje());
                String parte2 = StringUtils.replace(parte1,"NICK", m.getUsuario());
                String parte3 = StringUtils.replace(parte2,"FECHA",m.getFecha().toString());
                String parte4 = StringUtils.replace(parte3,"+"," ");
                String parte5 = StringUtils.replace(parte4,"%C2%BF","¿");
                String parte6 = StringUtils.replace(parte5,"%3F","?");
                String parte7 = StringUtils.replace(parte6,"%C3%B1","ñ");
                String parte8 = StringUtils.replace(parte7,"AVATAR",m.getAvatar());
                sb.append(StringUtils.replace(parte8, "CONTENT", m.getMensaje()));
                sb.append("\r\n");

            }
        }

        // Contenido completo
        final String content = readFile("index.html");

        final String contentChat = StringUtils.replace(content, "<!-- CHAT_CONTENT-->", sb.toString());

        // Envio el contenido
        IOUtils.write(contentChat + "\r\n", outputStream, Charset.defaultCharset());

    }

    public static String hex(byte[] array) {

        StringBuffer stringBuffer = new StringBuffer();

        for (int i = 0; i < array.length; ++i) {

            stringBuffer.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));

        }
        return stringBuffer.toString();

    }



}
