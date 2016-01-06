package serveur.element;

import java.util.HashMap;

public class Berserker extends Personnage {

	public Berserker(String nom, String groupe, HashMap<Caracteristique, Integer> carac) {
		super(nom, groupe, carac);
	}
	
	public void gainFo() {
		if(this.getCaract(Caracteristique.VIE) < 100) {
			int gain = (100 - getCaract(Caracteristique.VIE)) / 2;
			this.incrementeCaract(Caracteristique.FORCE, gain);
		}
	}
}
