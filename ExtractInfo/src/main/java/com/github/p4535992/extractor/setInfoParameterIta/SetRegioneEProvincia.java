package com.github.p4535992.extractor.setInfoParameterIta;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe che integra in modo completo la relazione Regione-Provincia-Citta.
 * @author 4535992.
 * @version 2015-07-03.
 */
@SuppressWarnings("unused")
public class SetRegioneEProvincia {
        
    private static List<String> abruzzo = new ArrayList<>();
    private static List<String> basilicata = new ArrayList<>();
    private static List<String> calabria = new ArrayList<>();
    private static List<String> campania = new ArrayList<>();
    private static List<String> emiliaromagna = new ArrayList<>();
    private static List<String> friuliveneziagiulia = new ArrayList<>();
    private static List<String> lazio = new ArrayList<>();
    private static List<String> liguria = new ArrayList<>();
    private static List<String> lombardia = new ArrayList<>();
    private static List<String> marche = new ArrayList<>();
    private static List<String> molise = new ArrayList<>();
    private static List<String> piemonte = new ArrayList<>();
    private static List<String> puglia = new ArrayList<>();
    private static List<String> sardegna = new ArrayList<>();
    private static List<String> sicilia = new ArrayList<>();
    private static List<String> toscana = new ArrayList<>();
    private static List<String> trentinoaltoadige = new ArrayList<>();
    private static List<String> umbria = new ArrayList<>();
    private static List<String> valleaosta = new ArrayList<>();
    private static List<String> veneto = new ArrayList<>();
    
    private static String provincia;
    private static String regione;
    
    public SetRegioneEProvincia(){
        abruzzo.add("Atessa");abruzzo.add("Avezzano");abruzzo.add("Campli");	
        abruzzo.add("Castel di Sangro");abruzzo.add("Celano");abruzzo.add("Chieti");	
        abruzzo.add("Fossacesia");abruzzo.add("Giulianova");abruzzo.add("L'Aquila");	
        abruzzo.add("Lanciano");abruzzo.add("Manoppello");abruzzo.add("Montesilvano");	
        abruzzo.add("Pescara");abruzzo.add("Pianella");abruzzo.add("Popoli");	
        abruzzo.add("San Salvo");abruzzo.add("Scurcola Marsicana");abruzzo.add("Spoltore");	
        abruzzo.add("Sulmona");abruzzo.add("Tagliacozzo");abruzzo.add("Teramo");	
        abruzzo.add("Chieti");    
        //*****************************************************************+
        basilicata.add("Avigliano");basilicata.add("Bernalda");basilicata.add("Ferrandina");	
        basilicata.add("Irsina"); basilicata.add("Lavello");basilicata.add("Maratea");	
        basilicata.add("Matera");basilicata.add("Melfi"); basilicata.add("Montescaglioso");	
        basilicata.add("Muro");basilicata.add("Pisticci");basilicata.add("Policoro");	
        basilicata.add("Potenza");basilicata.add("Rionero");basilicata.add("Tricarico");	
        basilicata.add("Tursi");basilicata.add("Tito");basilicata.add("Venosa");
        //********************************************************************+
        calabria.add("Acri");calabria.add("Amantea");calabria.add("Bisignano");	
        calabria.add("Castrovillari");calabria.add("Catanzaro");calabria.add("Corigliano"); 
        calabria.add("Cosenza");calabria.add("Crotone");calabria.add("Cutro");	
        calabria.add("Gerace");calabria.add("Gioia");calabria.add("Lamezia"); 
        calabria.add("Locri");calabria.add("Nicotera");calabria.add("Palmi");	
        calabria.add("Reggio Calabria");calabria.add("Petilia Policastro");	
        calabria.add("Rogliano");calabria.add("Rosarno");calabria.add("Rossano");	
        calabria.add("Soverato");calabria.add("Soveria Mannelli");calabria.add("Squillace");	
        calabria.add("Terranova Sappo Minulio");calabria.add("Vibo Valentia");	
        calabria.add("Villa San Giovanni");
        //*********************************************************************
        campania.add("Acerra");campania.add("Afragola");campania.add("Agropoli");	
        campania.add("Alife");campania.add("Amalfi");campania.add("Apollosa");	
        campania.add("Ariano Irpino");campania.add("Arzano");campania.add("Avellino");	
        campania.add("Aversa");campania.add("Battipaglia");campania.add("Bellona");	
        campania.add("Benevento");campania.add("Buonalbergo");campania.add("Campagna"); 	
        campania.add("Capri");campania.add("Capua");campania.add("Caserta");	
        campania.add("Castel Volturno");campania.add("Cava de'Tirreni");campania.add("Cervinara");	
        campania.add("Contursi Terme");campania.add("Eboli");campania.add("Ercolano");	
        campania.add("Fisciano");campania.add("Foglianise");campania.add("Frattamaggiore");	
        campania.add("Giffoni Valle Piana");campania.add("Giugliano in Campania");campania.add("Giugliano");
        campania.add("Grumo Nevano");campania.add("Marano di Napoli");campania.add("Marano");
        campania.add("Marcianise");campania.add("Marigliano");campania.add("Mignano Monte Lungo");	
        campania.add("Mignano");campania.add("Mirabella Eclano");campania.add("Moiano");	
        campania.add("Molinara");campania.add("Mondragone");campania.add("Montesarchio");	
        campania.add("Napoli");campania.add("Nocera Superiore");campania.add("Nocera");
        campania.add("Nola");campania.add("Pannarano");campania.add("Piano di Sorrento");	
        campania.add("Sorrento");campania.add("Pomigliano d'Arco");campania.add("Pompei");
        campania.add("Portici");campania.add("Qualiano");campania.add("Roccadaspide");
        campania.add("Roccarainola");campania.add("Sala Consilina");campania.add("Salerno");
        campania.add("Sant'Agata de' Goti");campania.add("Sapri");campania.add("Saviano");
        campania.add("Scafati");campania.add("Scisciano");campania.add("Solofra");
        campania.add("Teggiano");campania.add("Telese Terme");campania.add("Torre Annunziata");
        campania.add("Torre del Greco");campania.add("Venticano");campania.add("Vico Equense");
        //********************************************************************+
        emiliaromagna.add("Bobbio");emiliaromagna.add("Bologna");
        emiliaromagna.add("Bondeno");emiliaromagna.add("Busseto");
        emiliaromagna.add("Carpi");emiliaromagna.add("Castelfranco Emilia");
        emiliaromagna.add("Castel Maggiore");emiliaromagna.add("Castel San Pietro Terme");
        emiliaromagna.add("Castelnovo di Sotto");emiliaromagna.add("Cento"); 
        emiliaromagna.add("Cesena");emiliaromagna.add("Correggio");
        emiliaromagna.add("Crevalcore");emiliaromagna.add("Ferrara");
        emiliaromagna.add("Fidenza");emiliaromagna.add("Fiorano");
        emiliaromagna.add("Fiorenzuola d'Arda");emiliaromagna.add("Fiorenzuola");
        emiliaromagna.add("Forlì");emiliaromagna.add("Granarolo dell'Emilia");
        emiliaromagna.add("Guastalla");emiliaromagna.add("Imola");
        emiliaromagna.add("Maranello");emiliaromagna.add("Massa Lombarda");
        emiliaromagna.add("Meldola");emiliaromagna.add("Mirandola");
        emiliaromagna.add("Modena");emiliaromagna.add("Modigliana");
        emiliaromagna.add("Montecchio Emilia");emiliaromagna.add("Novellara");
        emiliaromagna.add("Parma");emiliaromagna.add("Piacenza");
        emiliaromagna.add("Predappio");emiliaromagna.add("Ravenna");
        emiliaromagna.add("Reggio Emilia");emiliaromagna.add("Riccione");
        emiliaromagna.add("Rimini");emiliaromagna.add("Riolo Terme");
        emiliaromagna.add("Russi");emiliaromagna.add("Salsomaggiore Terme");
        emiliaromagna.add("San Giovanni in Persiceto");emiliaromagna.add("Sasso Marconi");
        emiliaromagna.add("Sassuolo");emiliaromagna.add("Vignola");
        //********************************************************************+++
        friuliveneziagiulia.add("Casarsa della Delizia");friuliveneziagiulia.add("Cervignano del Friuli");
        friuliveneziagiulia.add("Cividale del Friuli");friuliveneziagiulia.add("Codroipo");
        friuliveneziagiulia.add("Cordenons");friuliveneziagiulia.add("Cormons");
        friuliveneziagiulia.add("Gemona del Friuli");friuliveneziagiulia.add("Gorizia");
        friuliveneziagiulia.add("Gradisca d'Isonzo");friuliveneziagiulia.add("Grado"); 
        friuliveneziagiulia.add("Latisana");friuliveneziagiulia.add("Lignano Sabbiadoro");
        friuliveneziagiulia.add("Maniago"); friuliveneziagiulia.add("Monfalcone");
        friuliveneziagiulia.add("Palmanova");friuliveneziagiulia.add("Porcia");
        friuliveneziagiulia.add("Pordenone");friuliveneziagiulia.add("Sacile");
        friuliveneziagiulia.add("San Daniele del Friuli");friuliveneziagiulia.add("Spilimbergo");
        friuliveneziagiulia.add("Tarcento");friuliveneziagiulia.add("Tolmezzo");
        friuliveneziagiulia.add("Trieste");friuliveneziagiulia.add("Udine");
        //**********************************************************************+
        lazio.add("Alvito");lazio.add("Anguillara Sabazia");lazio.add("Arpino");
        lazio.add("Castel Gandolfo");lazio.add("Ceccano");lazio.add("Ceprano");
        lazio.add("Ciampino");lazio.add("Civitavecchia");lazio.add("Cori");
        lazio.add("Fondi");lazio.add("Formia");lazio.add("Frascati");
        lazio.add("Ferentino");lazio.add("Frosinone");lazio.add("Gaeta");
        lazio.add("Genzano di Roma");lazio.add("Guidonia Montecelio");lazio.add("Ladispoli");
        lazio.add("Latina");lazio.add("Marino");lazio.add("Monte Porzio Catone");
        lazio.add("Monterotondo");lazio.add("Nettuno");lazio.add("Piedimonte San Germano");
        lazio.add("Pomezia");lazio.add("Priverno"); lazio.add("Rieti");
        lazio.add("Roma");lazio.add("Santa Marinella");lazio.add("Sezze");
        lazio.add("Tarquinia");lazio.add("Terracina");lazio.add("Tuscania");
        lazio.add("Velletri");lazio.add("Viterbo");
        //ENG NAME
        lazio.add("Rome");
        //************************************************************************
        liguria.add("Albenga"); liguria.add("Albisola Superiore"); liguria.add("Cairo Montenotte"); 
        liguria.add("Camogli"); liguria.add("Camporosso"); liguria.add("Chiavari"); 
        liguria.add("Finale Ligure"); liguria.add("Genova"); liguria.add("Imperia"); 
        liguria.add("Lavagna"); liguria.add("La Spezia"); liguria.add("Lerici"); liguria.add("Rapallo"); 
        liguria.add("Recco"); liguria.add("Sanremo"); liguria.add("Sarzana"); 
        liguria.add("Savona");liguria.add("Varazze"); liguria.add("Ventimiglia"); 
        //********************************************************************
        lombardia.add("Abbiategrasso");lombardia.add("Albino");lombardia.add("Alzano Lombardo");lombardia.add("Angera");
        lombardia.add("Appiano Gentile");lombardia.add("Arese");lombardia.add("Asola");lombardia.add("Belgioioso");
        lombardia.add("Bergamo");lombardia.add("Besana in Brianza");lombardia.add("Bollate");lombardia.add("Brescia");
        lombardia.add("Brugherio");lombardia.add("Busto Arsizio");lombardia.add("Calolziocorte");lombardia.add("Capriate San Gervasio");
        lombardia.add("Città di Caravaggio");lombardia.add("Caravaggio");lombardia.add("Casalmaggiore");lombardia.add("Casalpusterlengo");
        lombardia.add("Casorate Primo");lombardia.add("Cassano Magnago");lombardia.add("Castano Primo");lombardia.add("Casteggio");
        lombardia.add("Castel Goffredo");lombardia.add("Castellanza");lombardia.add("Castelleone");lombardia.add("Castiglione delle Stiviere");
        lombardia.add("Cernobbio");lombardia.add("Cernusco sul Naviglio");lombardia.add("Cesano Maderno");lombardia.add("Chiari");
        lombardia.add("Cinisello Balsamo");lombardia.add("Codogno");lombardia.add("Cologno Monzese");lombardia.add("Como");
        lombardia.add("Concorezzo");lombardia.add("Corbetta");lombardia.add("Corsico");lombardia.add("Crema");
        lombardia.add("Cremona");lombardia.add("Curtatone");lombardia.add("Dalmine");lombardia.add("Darfo Boario Terme");
        lombardia.add("Desenzano del Garda");lombardia.add("Desio");lombardia.add("Erba");lombardia.add("Gallarate");
        lombardia.add("Garbagnate Milanese");lombardia.add("Gardone Val Trompia");lombardia.add("Ghedi");lombardia.add("Giussano");
        lombardia.add("Gorgonzola");lombardia.add("Lainate");lombardia.add("Lecco");lombardia.add("Legnano");
        lombardia.add("Lissone");lombardia.add("Lodi");lombardia.add("Lodi Vecchio");lombardia.add("Lonato del Garda");
        lombardia.add("Luino");lombardia.add("Lurate Caccivio");lombardia.add("Magenta");lombardia.add("Mantova");
        lombardia.add("Manerbio");lombardia.add("Mariano");lombardia.add("Martinengo");lombardia.add("Meda");
        lombardia.add("Melegnano");lombardia.add("Melzo");lombardia.add("Merate");lombardia.add("Milano");
        lombardia.add("Montichiari");lombardia.add("Monza");lombardia.add("Morbegno");lombardia.add("Mortara");
        lombardia.add("Muggiò");lombardia.add("Novate Milanese");lombardia.add("Olgiate Comasco");lombardia.add("Orio al Serio");
        lombardia.add("Paderno Dugnano");lombardia.add("Palazzolo sull'Oglio");lombardia.add("Parabiago");lombardia.add("Paullo");
        lombardia.add("Pavia");lombardia.add("Peschiera Borromeo");lombardia.add("Pioltello");lombardia.add("Pontida");
        lombardia.add("Rho");lombardia.add("Rozzano");lombardia.add("Romano di Lombardia");lombardia.add("Salò");
        lombardia.add("Samarate");lombardia.add("San Donato Milanese");lombardia.add("San Giuliano Milanese");lombardia.add("San Martino Siccomario");
        lombardia.add("Sant'Angelo Lodigiano");lombardia.add("Saronno");lombardia.add("Segrate"); lombardia.add("Seregno");
        lombardia.add("Seriate");lombardia.add("Sesto Calende");lombardia.add("Sesto San Giovanni");lombardia.add("Seveso");
        lombardia.add("Somma Lombardo");lombardia.add("Soncino");lombardia.add("Sondrio"); lombardia.add("Soresina");
        lombardia.add("Sotto il Monte Giovanni XXIII");lombardia.add("Stradella");lombardia.add("Suzzara");lombardia.add("Tradate");
        lombardia.add("Travagliato");lombardia.add("Treviglio");lombardia.add("Trezzo sull'Adda");lombardia.add("Varese");
        lombardia.add("Varedo");lombardia.add("Valmadrera");lombardia.add("Vigevano");lombardia.add("Vimercate");
        lombardia.add("Voghera");
        //**********************************************************************************************++
        marche.add("Ancona");marche.add("Ascoli Piceno");marche.add("Camerino");marche.add("Castelplanio");
        marche.add("Corridonia");marche.add("Fabriano");marche.add("Fermo");marche.add("Grottammare");
        marche.add("Macerata");marche.add("Matelica");marche.add("Montalto delle Marche");marche.add("Montecassiano");
        marche.add("Osimo");marche.add("Ostra");marche.add("Porto Recanati");marche.add("Porto Sant'Elpidio");
        marche.add("Pergola");marche.add("Pesaro");marche.add("Recanati");marche.add("Ripatransone");
        marche.add("San Benedetto del Tronto");marche.add("San Severino Marche");marche.add("Senigallia");
        marche.add("Tolentino");marche.add("Treia");marche.add("Urbino");
        //****************************************************************************************************++
        molise.add("Bojano");molise.add("Campobasso città rosso"); molise.add("Campobasso"); 	
        molise.add("Isernia");molise.add("Larino");molise.add("Riccia");molise.add("Venafro"); 
        //***************************************************************************************************
        piemonte.add("Acqui Terme");piemonte.add("Alba");piemonte.add("Alessandria");piemonte.add("Arona");
        piemonte.add("Asti");piemonte.add("Avigliana");piemonte.add("Biella");piemonte.add("Borgaro Torinese");
        piemonte.add("Borgomanero");piemonte.add("Boves");piemonte.add("Bra");piemonte.add("Busca");
        piemonte.add("Candelo Biella");piemonte.add("Canelli");piemonte.add("Cannobio");piemonte.add("Carignano");
        piemonte.add("Carmagnola");piemonte.add("Casale Monferrato");piemonte.add("Caselle Torinese");
        piemonte.add("Castellamonte");piemonte.add("Cavallermaggiore");piemonte.add("Ceva");
        piemonte.add("Chieri");piemonte.add("Chivasso");piemonte.add("Cirié");piemonte.add("Collegno");
        piemonte.add("Cuneo");piemonte.add("Domodossola");piemonte.add("Fossano");piemonte.add("Garessio");
        piemonte.add("Gravellona Toce");piemonte.add("Ivrea");piemonte.add("Lanzo Torinese");piemonte.add("Moncalieri");
        piemonte.add("Moncalvo");piemonte.add("Mondovì");piemonte.add("Nichelino");piemonte.add("Nizza Monferrato");
        piemonte.add("Novara");piemonte.add("Novi Ligure");piemonte.add("Oleggio");piemonte.add("Omegna");
        piemonte.add("Orbassano");piemonte.add("Ormea");piemonte.add("Ovada");piemonte.add("Pinerolo");
        piemonte.add("Piossasco");piemonte.add("Racconigi");piemonte.add("Rivarolo Canavese");piemonte.add("Saluzzo");
        piemonte.add("San Mauro Torinese");piemonte.add("San Salvatore Monferrato");piemonte.add("Santena");
        piemonte.add("Savigliano");piemonte.add("Settimo Torinese");piemonte.add("Stresa");piemonte.add("Susa");
        piemonte.add("Torino");piemonte.add("Tortona");piemonte.add("Trecate");piemonte.add("Venaria Reale");
        piemonte.add("Verbania");piemonte.add("Vercelli");piemonte.add("Villadossola");
        //*******************************************************************************************************************+
        puglia.add("Alessano");puglia.add("Altamura");puglia.add("Andria");puglia.add("Apricena");
        puglia.add("Ascoli Satriano");puglia.add("Bari");puglia.add("Barletta");puglia.add("Bitetto");
        puglia.add("Bitonto");puglia.add("Brindisi");puglia.add("Campi Salentina");puglia.add("Canosa di Puglia");
        puglia.add("Casarano");puglia.add("Ceglie Messapica");puglia.add("Cerignola");puglia.add("Conversano");
        puglia.add("Fasano");puglia.add("Foggia");puglia.add("Francavilla Fontana");puglia.add("Galatina");
        puglia.add("Galatone");puglia.add("Ginosa");puglia.add("Grottaglie");puglia.add("Grumo Appula");
        puglia.add("Lecce");puglia.add("Latiano");puglia.add("Lucera");puglia.add("Maglie");puglia.add("Manfredonia");
        puglia.add("Massafra");puglia.add("Martina Franca");puglia.add("Matino");puglia.add("Melendugno");
        puglia.add("Mesagne");puglia.add("Miggiano");puglia.add("Modugno");puglia.add("Molfetta");
        puglia.add("Monopoli");puglia.add("Monteroni di Lecce");puglia.add("Muro Leccese");puglia.add("Nardò");
        puglia.add("Oria");puglia.add("Ostuni");puglia.add("Otranto");puglia.add("Poggiardo");puglia.add("Racale");
        puglia.add("Rodi Garganico");puglia.add("San Marco in Lamis");puglia.add("San Severo");puglia.add("Santa Cesarea Terme");
        puglia.add("San Vito dei Normanni");puglia.add("Squinzano");puglia.add("Taranto");puglia.add("Taurisano");
        puglia.add("Taviano");puglia.add("Trani");puglia.add("Trinitapoli");puglia.add("gento");puglia.add("Vernole");
        //**********************************************************************************************************
        sardegna.add("Alghero");sardegna.add("Bosa");sardegna.add("Cagliari");sardegna.add("Carbonia");
        sardegna.add("Castelsardo");sardegna.add("Iglesias"); sardegna.add("Ittiri");sardegna.add("Lanusei");
        sardegna.add("Macomer");sardegna.add("Nuoro");sardegna.add("Olbia");sardegna.add("Oristano");sardegna.add("Ozieri");
        sardegna.add("Quartu Sant'Elena");sardegna.add("Sassari");sardegna.add("Siniscola");sardegna.add("Sorso");
        sardegna.add("Tempio Pausania");sardegna.add("Tortolì");
        //****************************************************************************************************************+
        sicilia.add("Acireale");sicilia.add("Agrigento");sicilia.add("Barcellona Pozzo di Gotto");
        sicilia.add("Bivona");sicilia.add("Caltagirone");sicilia.add("Caltanissetta");sicilia.add("Canicattì");
        sicilia.add("Carini");sicilia.add("Castelvetrano");sicilia.add("Catania");sicilia.add("Cefalu");
        sicilia.add("Cefalù");sicilia.add("Enna");sicilia.add("Ispica");sicilia.add("Lentini");
        sicilia.add("Marsala");sicilia.add("Mazara del Vallo");sicilia.add("Messina");sicilia.add("Milazzo");
        sicilia.add("Mistretta");sicilia.add("Mineo");sicilia.add("Modica");sicilia.add("Monreale");
        sicilia.add("Naro");sicilia.add("Pachino");sicilia.add("Palermo");sicilia.add("Paternò");
        sicilia.add("Patti");sicilia.add("Piazza Armerina");sicilia.add("Porto Empedocle");sicilia.add("Ragusa");
        sicilia.add("San Cataldo");sicilia.add("Caltanissetta");sicilia.add("Siracusa");sicilia.add("Sciacca");
        sicilia.add("Taormina");sicilia.add("Trapani");
        //***************************************************************************************************************+
        toscana.add("Arezzo");toscana.add("Aulla");toscana.add("Barga");toscana.add("Camaiore");
        toscana.add("Carrara");toscana.add("Castelnuovo di Garfagnana");toscana.add("Chiusi");
        toscana.add("Colle di Val d'Elsa");toscana.add("Castiglion Fiorentino");toscana.add("Cortona");
        toscana.add("Fiesole");toscana.add("Firenze");toscana.add("Fivizzano");toscana.add("Follonica");
        toscana.add("Grosseto");toscana.add("Livorno");toscana.add("Lucca");toscana.add("Massa");
        toscana.add("Massa e Carrara");toscana.add("Massa Marittima");toscana.add("Montecatini Terme");
        toscana.add("Monte San Savino");toscana.add("Montevarchi");toscana.add("Montepulciano");
        toscana.add("Orbetello");toscana.add("Pescia");toscana.add("Pietrasanta");toscana.add("Piombino");
        toscana.add("Pisa");toscana.add("Pistoia");toscana.add("Pontedera");toscana.add("Pontremoli");
        toscana.add("Prato");toscana.add("Sansepolcro");toscana.add("Seravezza");toscana.add("Siena");
        toscana.add("Viareggio");toscana.add("Villafranca in Lunigiana");toscana.add("Volterra");
        //ENG NAME
        toscana.add("Florence");
        //**********************************************************************************************************
        trentinoaltoadige.add("Arco");trentinoaltoadige.add("Bolzano");trentinoaltoadige.add("Bressanone");
        trentinoaltoadige.add("Brunico");trentinoaltoadige.add("Klausen Suedtirol");trentinoaltoadige.add("Chiusa");	
        trentinoaltoadige.add("Glorenza");trentinoaltoadige.add("Laives");trentinoaltoadige.add("Levico Terme");
        trentinoaltoadige.add("Merano");trentinoaltoadige.add("Pergine Valsugana");trentinoaltoadige.add("Riva del Garda");
        trentinoaltoadige.add("Rovereto");trentinoaltoadige.add("Trento");trentinoaltoadige.add("Vipiteno");
        //****************************************************************************************************
        umbria.add("Amelia");umbria.add("Assisi");umbria.add("Città della Pieve");umbria.add("Foligno");
        umbria.add("Gualdo Tadino");umbria.add("Gubbio");umbria.add("Narni");umbria.add("Orvieto");
        umbria.add("Perugia");umbria.add("Terni");umbria.add("Todi");
        //***************************************************************************************
        valleaosta.add("Aosta");
        //*************************************************************************************************+
        veneto.add("Adria");veneto.add("Albignasego");veneto.add("Arzignano");veneto.add("Asolo");
        veneto.add("Badia Polesine");veneto.add("Bassano del Grappa");veneto.add("Belluno");
        veneto.add("Bovolone");veneto.add("Campodarsego");veneto.add("Camposampiero");veneto.add("Castelfranco Veneto");
        veneto.add("Cerea");veneto.add("Chioggia");veneto.add("Cittadella");veneto.add("Cologna Veneta");
        veneto.add("Conegliano");veneto.add("Eraclea");veneto.add("Este");veneto.add("Feltre");
        veneto.add("Jesolo");veneto.add("Lendinara");veneto.add("Lonigo");veneto.add("Marostica");
        veneto.add("Mestre");veneto.add("Mirano");veneto.add("Mogliano Veneto");
        veneto.add("Montagnana");veneto.add("Montebelluna");veneto.add("Montegrotto Terme");veneto.add("Motta di Livenza");
        veneto.add("Oderzo");veneto.add("Oppeano");veneto.add("Padova");veneto.add("Piove di Sacco");
        veneto.add("Portogruaro");veneto.add("Porto Viro");veneto.add("Roncade");veneto.add("Rovigo");
        veneto.add("San Donà di Piave");veneto.add("San Martino di Lupari");veneto.add("Schio");
        veneto.add("Treviso");veneto.add("Valdagno");veneto.add("Venezia");veneto.add("Verona");
        veneto.add("Vicenza");veneto.add("Villafranca di Verona");veneto.add("Villorba");veneto.add("Vittorio Veneto");
    }

    public String getProvincia() {
        return provincia;
    }

    public String getRegione() {
        return regione;
    }

    public static void setProvincia(String provincia) {
        SetRegioneEProvincia.provincia = provincia;
    }

    public static void setRegione(String regione) {
        SetRegioneEProvincia.regione = regione;
    }
    
    
 
    
    public void checkString(String province){
        if(checkList(abruzzo , province)){setProvincia(province);setRegione("Abruzzo");}
        else if(checkList(basilicata , province)){setProvincia(province);setRegione("Basilicata");}
        else if(checkList(calabria , province)){setProvincia(province);setRegione("Calabria");}
        else if(checkList(campania , province)){setProvincia(province);setRegione("Campania");}
        else if(checkList(emiliaromagna , province)){setProvincia(province);setRegione("Emilia Romagna");}
        else if(checkList(friuliveneziagiulia , province)){setProvincia(province);setRegione("Friuli Venezia Giulia");}
        else if(checkList(lazio , province)){setProvincia(province);setRegione("Lazio");}
        else if(checkList(liguria , province)){setProvincia(province);setRegione("Liguria");}
        else if(checkList(lombardia , province)){setProvincia(province);setRegione("Lombardia");}
        else if(checkList(marche , province)){setProvincia(province);setRegione("Marche");}
        else if(checkList(molise , province)){setProvincia(province);setRegione("Molise");}
        else if(checkList(piemonte , province)){setProvincia(province);setRegione("Piemonte");}
        else if(checkList(puglia , province)){setProvincia(province);setRegione("Puglia");}
        else if(checkList(sardegna , province)){setProvincia(province);setRegione("Sardegna");}
        else if(checkList(sicilia , province)){setProvincia(province);setRegione("Sicilia");}
        else if(checkList(toscana , province)){setProvincia(province);setRegione("Toscana");}
        else if(checkList(trentinoaltoadige , province)){setProvincia(province);setRegione("Trentino Alto Adige");}
        else if(checkList(umbria , province)){setProvincia(province);setRegione("Umbria");}
        else if(checkList(valleaosta , province)){setProvincia(province);setRegione("Valle d'Aosta");}
        else if(checkList(veneto, province)){setProvincia(province);setRegione("Veneto");}
        else {setProvincia(null);setRegione(null);}
        
        
        //System.out.println("[CITY:"+province+" PROVINCIA:"+getProvincia()+" REGIONE:"+getRegione()+"]");
//        abruzzo.clear(); 
//        basilicata.clear(); 
//        calabria.clear(); 
//        campania.clear(); 
//        emiliaromagna.clear(); 
//        friuliveneziagiulia.clear(); 
//        lazio.clear(); 
//        liguria.clear(); 
//        lombardia.clear(); 
//        marche.clear(); 
//        molise.clear(); 
//        piemonte.clear(); 
//        puglia.clear(); 
//        sardegna.clear(); 
//        sicilia.clear(); 
//        toscana.clear(); 
//        trentinoaltoadige.clear(); 
//        umbria.clear(); 
//        valleaosta.clear(); 
//        veneto.clear(); 
    }
    
    
    private boolean checkList(List<String> listRegioni,String province){
        boolean b =false;
        for(String s: listRegioni){
            if(s.replaceAll("\\s+","").equalsIgnoreCase(province.replaceAll("\\s+",""))) {
                    // this will also take care of spaces like tabs etc.
                    b = true;
                    break;
            }else if(s.contains(province.replaceAll("\\s+",""))){
                    b = true;
                    break;
            }else{
                b = false;
            }
        }
        return b;
    }

    public static List<String> getAbruzzo() {
        return abruzzo;
    }

    public static List<String> getBasilicata() {
        return basilicata;
    }

    public static List<String> getCalabria() {
        return calabria;
    }

    public static List<String> getCampania() {
        return campania;
    }

    public static List<String> getEmiliaromagna() {
        return emiliaromagna;
    }

    public static List<String> getFriuliveneziagiulia() {
        return friuliveneziagiulia;
    }

    public static List<String> getLazio() {
        return lazio;
    }

    public static List<String> getLiguria() {
        return liguria;
    }

    public static List<String> getLombardia() {
        return lombardia;
    }

    public static List<String> getMarche() {
        return marche;
    }

    public static List<String> getMolise() {
        return molise;
    }

    public static List<String> getPiemonte() {
        return piemonte;
    }

    public static List<String> getPuglia() {
        return puglia;
    }

    public static List<String> getSardegna() {
        return sardegna;
    }

    public static List<String> getSicilia() {
        return sicilia;
    }

    public static List<String> getToscana() {
        return toscana;
    }

    public static List<String> getTrentinoaltoadige() {
        return trentinoaltoadige;
    }

    public static List<String> getUmbria() {
        return umbria;
    }

    public static List<String> getValleaosta() {
        return valleaosta;
    }

    public static List<String> getVeneto() {
        return veneto;
    }
}
