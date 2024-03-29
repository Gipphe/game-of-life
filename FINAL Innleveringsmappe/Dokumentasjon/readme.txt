﻿Utførte oppgaver:

* Korrekt implementasjon av GoL
	* Støtte for animasjon etter spillereglene til GoL.

	* Implementert et grafisk brukergrensesnitt med JavaFX/FXML som er brukervennlig og har 
	  god estetisk kvalitet.

* Filbehandling
	* Støtte for innlastning av GoL mønstre med bruk av .RLE filformat og URL kode.
	
* Bruk av Java Collections for brettstruktur
	* Støtte for både statisk og dynamisk brett.

* Bruk av flere tråder for å oppnå forbedret ytelse
	* Observerbar bedre ytelse når man bruker flere tråder på store brett.
	  En generasjon av log(t) growth tar ~14 sekunder (median av 20 tester) 
	  med fire tråder, sammenlignet med en median av 39 sekunder med kun en tråd.
	  Bruker 1-3 generasjoner får den normaliserer seg.

* Utvidelsesoppgaver
	* Implementasjon av Android app med støtte for "text-to-QR" GoL brett
	* Manipuleringseditor og lagring til .GIF og .RLE
		* Importerer farger og tegner en fremvisning på "Strip"-en. (fargene til
		  hovedbrettet blir satt til svart og hvit for å gjøre den lettere å se og
		  endre på. "Strip" og GIFs blir tegnet med de brukerdefinerte fargene.
		* Sender brettet tilbake til hoved GoL-en dersom brukeren ønsker det.
		* Strip re-tegnes automatisk etter hver endring brukeren gjør
		* Dynamisk hovedbrett (der brukeren kan manipulere mønsteret)
	* Tastatur-funksjoner og mus-funksjonalitet
		* W, A, S og D knappene flytter brettet dersom brettet ikke er
		  "zoomed" helt ut (da blir den sentrert). 
			* Shift-knappen øker farten på flyttingen mens den holdes ned. 
			* Q er toggle for Start/Stop og E kjører "next frame".
		* Venstreklikk bytter cellenes tilstand (m/ støtte for intuitiv
		  "dragging"). Høyreklikk-og-dra flytter "scope"-en til canvas 
		  (panning), og slutter å tegne celler som er utenfor "scope".
	* "Custom" spillregler
		* brukerdefinerte spillregler
		* bibliotek med forhåndsdefinerte spillregler