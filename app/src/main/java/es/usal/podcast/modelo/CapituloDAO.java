package es.usal.podcast.modelo;

import java.util.List;

/**
 * Interfaz del patrón DAO de Capitulo
 * @author Jorge Alonso Merchán
 */
public interface CapituloDAO {

    public List<Capitulo> getCapitulos(int programaid);
    public List<Capitulo> getCapitulosEscuchados(int usuarioid);
    public List<Capitulo> getCapitulosMasEscuchados(int programaId);
    public List<Capitulo> getUltimosCapitulos();
    public List<Capitulo> getUltimosCapitulosSubscripciones(String token);
    public List<Capitulo> getDescargas();
    public List<Capitulo> getBusqueda(String text);
    public List<Timeline> getTimeline(String token);
    public int addCapituloEscuchado(String token, int capituloid);
    public int getTiempoEscuchado(int capituloid);
    public int updateTiempoEscuchado(int capituloid, int tiempo);
    public int addDescarga(Capitulo capitulo);
    public int deleteDescarga(int capituloid);
    public void deleteDescargas();

}
