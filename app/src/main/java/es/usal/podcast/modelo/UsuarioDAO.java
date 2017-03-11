package es.usal.podcast.modelo;

import android.content.Context;

import java.util.List;

/**
 * Interfaz del patrón DAO de Usuario
 * @author Jorge Alonso Merchán
 */
public interface UsuarioDAO {

    public Usuario getUsuario(String token);
    public List<Usuario> getSubscriptores(int programaid);
    public Usuario getUsuario(int usuarioid);
    public List<Usuario> getSeguidores(int usuarioid);
    public List<Usuario> getSeguidos(int usuarioid);
    public List<Usuario> getBusqueda(String text);
    public int registrar(String nombre, String correo, String password);
    public String nuevoUsuarioAnonimo();
    public int login(Context context, String correo, String password);
    public Usuario updateUsuario(Usuario usuario, String token);
    public Usuario updateUsuarioPasswordNuevo(Usuario usuario, String password, String token);



    public int addSeguir(String token, int usuarioid);
    public boolean leSigo(String token, int usuarioid);
    public int deleteSeguir(String token, int usuarioid);
    public void cerrarSesion(String token);



}
