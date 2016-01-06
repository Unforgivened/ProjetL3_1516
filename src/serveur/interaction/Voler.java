package serveur.interaction;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.logging.Level;

import serveur.Arene;
import serveur.element.Caracteristique;
import serveur.vuelement.VuePersonnage;
import utilitaires.Constantes;

public class Voler extends Interaction<VuePersonnage> {
	
	public Voler(Arene arene, VuePersonnage soigneur, VuePersonnage cible) {
		super(arene, soigneur, cible);
	}

	@Override
	public void interagit() {
		try {
			logs(Level.INFO, Constantes.nomRaccourciClient(attaquant) + " va soigner " + 
					Constantes.nomRaccourciClient(defenseur));
			
			// si le personnage est vivant
			if(attaquant.getElement().estVivant()) {

				// caracteristiques de la cible
				HashMap<Caracteristique, Integer> valeursCible = defenseur.getElement().getCaracts();
				if(valeursCible.get(Caracteristique.FORCE) >= 15) {
					arene.incrementeCaractElement(defenseur, Caracteristique.FORCE, -15);
					arene.incrementeCaractElement(attaquant, Caracteristique.FORCE, 15);
				}
				else {
					logs(Level.INFO, "Impossible de voler !");
				}
				
				logs(Level.INFO, "Ralentissement effectue");
				
			} else {
				logs(Level.INFO, Constantes.nomRaccourciClient(attaquant) + " ou " + 
						Constantes.nomRaccourciClient(defenseur) + " est deja mort... Rien ne se passe");
			}
		} catch (RemoteException e) {
			logs(Level.INFO, "\nErreur lors d'un soin : " + e.toString());
		}
	}
}
