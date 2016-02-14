package pl.com.vrill7.ibanfield;

import java.awt.Color;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.logging.Level;
import javax.swing.JFormattedTextField;
import javax.swing.border.LineBorder;
import javax.swing.text.MaskFormatter;

/**
 * @author  Piotr Karasiński e-mail:parzno@o2.pl
 *
 */
public class IbanField extends JFormattedTextField{

    /*
        NRB (Numer Rachunku Bankowego) jest polskim standardem określającym sposób numeracji rachunków bankowych. NRB składa się z 26 cyfr, jego struktura jest następująca:
        CCAAAAAAAABBBBBBBBBBBBBBBB

        gdzie:
        CC to suma kontrolna (2 cyfry),
        AAAAAAAA to numer rozliczeniowy jednostki organizacyjnej banku (8 cyfr),
        BBBBBBBBBBBBBBBB to numer rachunku klienta w banku (16 cyfr).

        W formie elektronicznej używa się zapisu, jaki przedstawiono powyżej, natomiast w postaci papierowej umieszcza się w numerze spacje zwiększające czytelność:
        CC AAAA AAAA BBBB BBBB BBBB BBBB
     */
    public IbanField() {
	super();

	MaskFormatter mf1 = null;

	try
	//                           CC AAAA AAAA BBBB BBBB BBBB BBBB
	{
	    mf1 = new MaskFormatter("## #### #### #### #### #### ####");
	}
	catch (ParseException e){
            java.util.logging.Logger.getLogger("").log(Level.SEVERE, "Niepoprawna maska dla pola typu nr. rachunku bankowego", e);
//            org.slf4j.LoggerFactory.getLogger(IbanField.class.getName()).error("Niepoprawna maska dla pola typu nr. rachunku bankowego", e);
        }
	mf1.install(this); // nie wymusza wpisywanie wszystkich znakow z maski
	this.setToolTipText("Wpisz numer konta w formacie NRB (CC AAAA AAAA BBBB BBBB BBBB BBBB)");
	this.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
	    public void keyReleased(java.awt.event.KeyEvent e) {
		textValueChanged();
	    }
	});
    }

    private void textValueChanged() {
	String s = this.getText();

	if (isNrb(s)) {
	    this.setBorder( new LineBorder(Color.GREEN, 2) );
	} else {
	    this.setBorder( new LineBorder(Color.RED, 2) );
	}
    }

    public String getNrb() {
	if (isNrb(this.getText())) {
	    return this.getText().toUpperCase().replaceAll("[\\p{Space}]*", "");
	}
	return null;
    }

    private boolean isNrb(String nrb) {
	// kod kraju PL 25 21 | P = 25, L = 21
	nrb = nrb.toUpperCase().replaceAll("[\\p{Space}]*", "");
	//1. sprawdzamy dlugosc, tu akurat bez kodu kraju
	if (nrb.length() == 26) {
	    //2.Przenieś 4 pierwsze znaki numeru na jego koniec. | 4 ale razem z kodem kraje czyli PLXX - gdzie xx to suma kontrolna
	    nrb = nrb.substring(2,26) + "2521" +nrb.substring(0,2);
//	    System.out.println("nrb k2: " + nrb);
	    BigInteger lnrb =  new BigInteger(nrb);
	    if (lnrb.mod(new BigInteger("97")).intValue() == 1) {
		return true;
	    }
	}
	return false;
    }
}
