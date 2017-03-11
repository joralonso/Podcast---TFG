package es.usal.podcast.modelo;

import java.util.List;

/**
 * Interfaz del patrón DAO de Radio
 * @author Jorge Alonso Merchán
 */
public interface RadioDAO {

    public List<Radio> getRadios();
    public List<Radio> getBusqueda(String text);

}
