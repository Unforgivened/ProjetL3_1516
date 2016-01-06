package serveur.interaction;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.logging.Level;

import serveur.Arene;
import serveur.element.Caracteristique;
import serveur.vuelement.VuePersonnage;
import serveur.vuelement.VuePotion;
import utilitaires.Constantes;

public class Soin extends Interaction<VuePersonnage> {
	/**
	 * Cree une interaction de ramassage.
	 * @param arene arene
	 * @param ramasseur personnage ramassant la potion
	 * @param potion potion a ramasser
	 */
	public Soin(Arene arene, VuePersonnage soigneur, VuePersonnage cible) {
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
				if(valeursCible.get(Caracteristique.VIE) < 80) {
				arene.incrementeCaractElement(defenseur, Caracteristique.VIE, 20);
				}
				else if(valeursCible.get(Caracteristique.VIE) < 100) {
					arene.incrementeCaractElement(defenseur, Caracteristique.VIE, 100 - valeursCible.get(Caracteristique.VIE));
				}
				else {
					logs(Level.INFO, "Deja au maximum de ses HP !");
				}
				
				logs(Level.INFO, "Soin effectue");
				
			} else {
				logs(Level.INFO, Constantes.nomRaccourciClient(attaquant) + " ou " + 
						Constantes.nomRaccourciClient(defenseur) + " est deja mort... Rien ne se passe");
			}
		} catch (RemoteException e) {
			logs(Level.INFO, "\nErreur lors d'un soin : " + e.toString());
		}
	}
}
