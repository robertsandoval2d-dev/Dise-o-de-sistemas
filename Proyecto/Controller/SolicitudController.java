package Controller;

import Servicios.ISolicitudCommandService;
import Servicios.ISolicitudQueryService;

public class SolicitudController{
    //Atributos privados
    private ISolicitudCommandService command;
    private ISolicitudQueryService query;

    //Constructor
    public SolicitudController(ISolicitudCommandService solicitudCommandService,ISolicitudQueryService solicitudQueryService){
        this.command = solicitudCommandService;
        this.query = solicitudQueryService;
    }

    //APIS 

}