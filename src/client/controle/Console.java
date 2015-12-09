package client.controle;

import java.awt.Point;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.logging.Level;

import client.StrategiePersonnage;
import logger.MyLogger;
import serveur.IArene;
import serveur.element.Personnage;
import serveur.vuelement.VueElement;
import utilitaires.Constantes;

/**
 * Implementation des methodes RMI associees au controle d'un personnage.
 * La strategie est executee dans la methode run(). 
 *
 */
public class Console extends UnicastRemoteObject implements IConsole {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Adresse IP du serveur.
	 */
	private String ipArene;

	/**
	 * Port de communication avec l'arene.
	 */
	private int port;

	/**
	 * Adresse IP de la console.
	 */
	private String ipConsole;

	/**
	 * Arene (serveur) avec lequel la console communique. 
	 */
	private IArene arene = null;

	/**
	 * Reference attribuee par le serveur a la connexion.
	 */
	private int refRMI;

	/**
	 * Strategie jouee par l'element correspondant. 
	 */
	private final StrategiePersonnage strategiePer;

	/**
	 * Gestionnaire de log.
	 */
	private MyLogger logger;
	
	/**
	 * Cree une console associee a la strategie d'un personnage. 
	 * @param ipArene ip de communication avec l'arene
	 * @param port port de communication avec l'arene
	 * @param ipConsole ip de la console
	 * @param strategiePer strategie du personnage 
	 * @param pers personnage 
	 * @param nbTours nombre de tours pour ce personnage (si negatif, illimite)
	 * @param position position initiale du personnage dans l'arene
	 * @param logger gestionnaire de log
	 * @throws RemoteException
	 */
	public Console(String ipArene, int port, String ipConsole,
			StrategiePersonnage strategiePer, Personnage pers, long nbTours, 
			Point position, MyLogger logger) throws RemoteException {
		
		super();
		this.port = port;
		this.ipArene = ipArene;
		this.strategiePer = strategiePer;
		this.ipConsole = ipConsole;
		this.logger = logger;
		
		// initialisation de l'element pour lequel le controleur est cree
		try {
			// preparation connexion au serveur
			// handshake/enregistrement RMI
			logger.info(Constantes.nomClasse(this), "Tentative de recuperation de l'arene...");
			arene = (IArene) java.rmi.Naming.lookup(Constantes.nomRMI(this.ipArene, this.port, "Arene"));
			logger.info(Constantes.nomClasse(this), "Arene recuperee");

			// initialisation de la reference du controleur sur le serveur
			// La console devient "serveur" pour les methodes de IConsole 
			// lancer l'annuaire RMI en tant que serveur
			// a faire une seule fois par serveur de console pour un port donne
			// doit rester "localhost"
			logger.info(Constantes.nomClasse(this), "Demande d'allocation de port");
			this.refRMI = arene.allocateRefRMI();
			int portServeur = this.port + refRMI;
			logger.info(Constantes.nomClasse(this), "Port alloue : " + portServeur);
			java.rmi.registry.LocateRegistry.createRegistry(portServeur);
			Naming.rebind(adrToString(), this);
			
			// connexion a l'arene pour lui permettre d'utiliser les methodes de IConsole
			logger.info(Constantes.nomClasse(this), "Demande de connexion avec l'adresse " + adrToString());
			
			boolean resultatConnexion = arene.connecte(refRMI, ipConsole, pers, nbTours, position);
			
			if (!resultatConnexion) {
				logger.severe(Constantes.nomClasse(this), "Echec de connexion");
				System.exit(1);
			}
			
			setPhrase("Atterrissage ...");
			
			// affiche message si succes
			logger.info(Constantes.nomClasse(this), "Connexion reussie");
			
 		} catch (Exception e) {
 			logger.severe(Constantes.nomClasse(this), "Erreur : Impossible de creer la console :\n"+e.toString());
  			e.printStackTrace();
  			System.exit(1);
 		}
	}

	@Override
	public void run() throws RemoteException {
		// met a jour les voisins 
		HashMap<Integer, Point> voisins = arene.getVoisins(refRMI);
		
		// applique la strategie du personnage
		strategiePer.strategie(voisins);
	}


	@Override
	public void shutDown(String cause) throws RemoteException {
		logger.info(Constantes.nomClasse(this), "Console deconnectee : " + cause);
		System.exit(0);
	}


	@Override
	public IArene getArene() throws RemoteException {
		return arene;
	}

	@Override
	public int getRefRMI() throws RemoteException{
		return refRMI;
	}

	@Override
	public StrategiePersonnage getStrategiePer() throws RemoteException {
		return strategiePer;
	}

	@Override
	public Personnage getPersonnageServeur() throws RemoteException {
		return (Personnage) arene.elementFromConsole(this);
	}

	@Override
	public VueElement getVueElement() throws RemoteException {
		return arene.vueFromConsole(this);
	}

	@Override
	public void setPhrase(String s) throws RemoteException {
		arene.setPhrase(this, s);
	}

	@Override
	public String adrToString() throws RemoteException {
		return Constantes.nomRMI(ipConsole, port + refRMI, "Console" + refRMI);
	}

	@Override
	public void log(Level level, String prefixe, String msg) throws RemoteException {
		logger.log(level, prefixe, msg);
	}
}