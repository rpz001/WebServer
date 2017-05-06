import java.util.Date;

/**
 * Created by RodrigoPizarro on 06-04-2017.
 * Clase que representa un mensaje enviado por un usuario.
 */
public class Mensaje {

    /**
     * Direccion IP de donde se envio el mensaje.
     */
    private String ip;

    /**
     * Email del usuario.
     */
    private String email;

    /**
     * Nombre del usuario.
     */
    private String usuario;

    /**
     * Ruta donde se aloja un avatar (Gravatar).
     */
    private String avatar;

    /**
     * Fecha en que se publico el mensaje.
     */
    private Date fecha;

    /**
     * Contenido del mensaje.
     */
    private String mensaje;

    /**
     * Establece la IP.
     * @param i La direccion IP.
     */
    public void setIp(String i){

        this.ip = i;

    }

    /**
     * Establece el email.
     * @param e El correo electronico.
     */
    public void setEmail(String e){

        this.email = e;

    }

    /**
     * Establece el nombre de usuario.
     * @param u Nick de usuario.
     */
    public void setUsuario(String u){

        this.usuario = u;

    }

    /**
     * Establece la ruta donde se aloja el avatar.
     * @param a Ruta de gravatar.
     */
    public void setAvatar(String a){

        this.avatar = a;

    }

    /**
     * Establece la fecha en que se publico el mensaje.
     * @param f Fecha de publicacion.
     */
    public void  setFecha(Date f){

        this.fecha = f;

    }

    /**
     * Establece el contenido del mensaje.
     * @param m Contenido del mensaje.
     */
    public void setMensaje(String m){

        this.mensaje = m;

    }

    /**
     * Obtiene la dirección IP de donde se publico el mensaje.
     * @return Direccion IP
     */
    public String getIp(){

        return ip;

    }

    /**
     * Obtiene el email del usuario que envio el mensaje.
     * @return Email de usuario
     */
    public String getEmail(){

        return email;

    }

    /**
     * Obtiene el nombre (o nick) del usuario.
     * @return Nick de usuario.
     */
    public String getUsuario(){

        return usuario;

    }

    /**
     * Obtiene la direccion donde se aloja el avatar (Gravatar)
     * @return Direccion donde se aloja Gravatar.
     */
    public String getAvatar(){

        return avatar;

    }

    /**
     * Obtiene la fecha en que se publico el mensaje.
     * @return Fecha en que se publico el mensaje.
     */
    public Date getFecha(){

        return fecha;

    }

    /**
     * Obtiene el contenido del mensaje.
     * @return Contenido del mensaje.
     */
    public String getMensaje(){

        return mensaje;

    }


}
