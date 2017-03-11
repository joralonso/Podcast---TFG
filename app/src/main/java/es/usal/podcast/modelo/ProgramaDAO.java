package es.usal.podcast.modelo;

import java.util.List;

/**
 * Interfaz del patrón DAO de Programa
 * @author Jorge Alonso Merchán
 */
public interface ProgramaDAO {


    public List<Programa> getSubscripciones(String token);
    public List<Programa> getUsuarioSubscripciones(int usuarioid);
    public List<Programa> getBusqueda(String text);
    public List<Programa> getRelacionados(int programaid);
    public List<Programa> getDestacados();

    public Programa addPrograma(String rss);
    public int addSubscripcion(String token, int programaid);
    public boolean estoySubscrito(String token, int programaid);
    public int deleteSubscripcion(String token, int programaid);



}
