Utf�rte oppgaver (u/ � ta hensyn til * Kvalitet p� kildekode)

* Korrekt implementasjon av GoL
	* St�tte for animasjon etter spillereglene til GoL.

	* Implementert et grafisk brukergrensesnitt med JavaFX/FXML som er brukervennlig og har god estetisk kvalitet.

* Filbehandling
	* St�tte for innlastning av GoL m�nstre med bruk av .RLE filformat og URL kode.
	
* Bruk av Java Collections for brettstruktur
		* St�tte for b�de statisk og dynamisk brett.
			* Statisk: brettet "wrap"-er da m�nsteret n�r et hj�rne.
			* Dynamisk: brettet f�r et rad eller kolonne lagt til seg da m�nsteret n�r et hj�rne, 
			  og forblir rektangul�r. 

* Bruk av flere tr�der for � oppn� forbedret ytelse
	* Observerbar bedre ytelse n�r man bruker flere tr�der p� store brett. 
	  En generasjon av log(t) growth tar ~14 sekunder (median av 20 tester) 
	  med fire tr�der, sammenlignet med en median av 39 sekunder med kun en tr�d. 
	  Bruker 1-3 generasjoner f�r den normaliserer seg.

* Utvidelsesoppgaver
	* Implementasjon av Android app med st�tte for "text-to-QR" GoL brett.
	* Manipuleringseditor og lagring til .GIF og .RLE 
	* "Custom" spillregler
		* brukerdefinerte spillregler
		* bibliotek med forh�ndsdefinerte spillregler