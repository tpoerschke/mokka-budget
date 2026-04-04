<!-- Improved compatibility of back to top link: See: https://github.com/othneildrew/Best-README-Template/pull/73 -->
<a name="readme-top"></a>

<!-- PROJECT SHIELDS -->
<!--
*** I'm using markdown "reference style" links for readability.
*** Reference links are enclosed in brackets [ ] instead of parentheses ( ).
*** See the bottom of this document for the declaration of the reference variables
*** for contributors-url, forks-url, etc. This is an optional, concise syntax you may use.
*** https://www.markdownguide.org/basic-syntax/#reference-style-links
-->
<!--
[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
[![MIT License][license-shield]][license-url]
[![LinkedIn][linkedin-shield]][linkedin-url]
-->


<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/tpoerschke/mokka-budget">
    <img src="images/MOKKA-Budget-Logo.png" alt="Logo" width="140" height="140">
  </a>

<h3 align="center">MOKKA Budget</h3>

  <p align="center">
    Monitoring, Organisierung, Kontrolle, Kategorisierung & Analyse  – Das Haushaltsbuch für volle Finanzkontrolle!
    <!--
    <br />
    <a href="https://github.com/github_username/repo_name"><strong>Explore the docs »</strong></a>
    <br />
    <br />
    <a href="https://github.com/github_username/repo_name">View Demo</a>
    ·
    <a href="https://github.com/github_username/repo_name/issues">Report Bug</a>
    ·
    <a href="https://github.com/github_username/repo_name/issues">Request Feature</a>
    -->
  </p>
</div>



<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#über-das-projekt">Über das Projekt</a>
      <ul>
        <li><a href="#screenshots">Screenshots</a></li>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#installation">Installation</a>
    </li>
    <li>
      <a href="#contributing">Contributing</a>
      <ul>
        <li><a href="#entwickeln">Entwickeln</a></li>
        <li><a href="#bauen">Bauen</a></li>
      </ul>
    </li>
    <li><a href="#roadmap">Roadmap</a></li>
    <li>
        <a href="#contributing">Contributing</a>
        <ul>
            <li><a href="#entwickeln">Entwickeln</a></li>
            <li><a href="#bauen">Bauen</a></li>
            <li><a href="#your-feature-or-enhancement">Your feature or enhancement</a></li>
        </ul>
    </li>
    <li><a href="#license">License</a></li>
    <li><a href="#contact">Contact</a></li>
    <!--<li><a href="#acknowledgments">Acknowledgments</a></li>-->
  </ol>
</details>



<!-- ABOUT THE PROJECT -->

## Über das Projekt

[![MOKKA-Budget-MainView][product-screenshot-1]](https://github.com/tpoerschke/mokka-budget)

In Zeiten vieler Abonnements und weitverbreiteter Kartenzahlung kann man schnell die Übersicht über seine Ausgaben verlieren.
Dabei unterstützt dich dieses Haushaltsbuch, indem es die Nachverfolgung – via Import oder manueller Pflege –, Planung und Analyse deiner Ausgaben ermöglicht.

Features:
- **Planung** von wiederkehrenden Ausgaben (und Einnahmen) (**Fixkosten**)
- **Nachverfolgung realer Ausgaben** (und Einnahmen) durch Import oder manueller Pflege
- **Kategorisierung** von Ausgaben (und Einnahmen)
- Verwaltung von **Budgets je Kategorie**
- **Analyse**, die Entwicklung einer Ausgabenkategorie darstellt

Und das beste: **Keine Cloud** und keine amerikanischen Dienste. **Deine Daten** werden lokal **auf deinem Rechner** verarbeitet und gespeichert.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

### Screenshots

[![MOKKA-Budget-AnnualOverview][product-screenshot-2]](https://github.com/tpoerschke/mokka-budget)
[![MOKKA-Budget-AnalysisView][product-screenshot-3]](https://github.com/tpoerschke/mokka-budget)

### Built With

[![OpenJDK][OpenJDK-shield]][OpenJDK-url]
[![Maven][Maven-shield]][Maven-url]
[![Hibernate][Hibernate-shield]][Hibernate-url]
[![SQLite][SQLite-shield]][SQLite-url]
[![Lombok][Lombok-shield]][Lombok-url]
[![Dagger2][Dagger-shield]][Dagger-url]
[![SonarQube][SonarQube-shield]][SonarQube-url]


<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- GETTING STARTED -->

## Installation

Die Anwendung steht beim aktuellen Release zum Download bereit: https://github.com/tpoerschke/mokka-budget/releases

Anleitung für Mac:

1. Das aktuelle Release als ZIP herunterladen und entpacken
2. Die App ins Programme-Verzeichnis verschieben
3. App starten

Hinweis: Ggf. muss man die Ausführung explizit erlauben, wenn MacOS meldet, dass es die Anwendung (noch) nicht auf Schadsoftware o. ä. überprüfen kann.
In den Systemeinstelllungen (Systemeinstellungen > Datenschutz & Sicherheit) kann die Ausführung der App erlaubt werden, sobald ein Versuch unternommen wurde, sie
auszuführen.

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- ROADMAP -->
## Roadmap

- [x] Monatsübersicht
- [x] Jahresübersicht
- [x] Verwaltung von Umsätzen
- [x] Kategoriesystem
- [x] Import von Umsätzen
- [x] Budgets
- [x] Grundlegende Analyse (Balkendiagramm pro Kategorie)
- [ ] Burn-Up-Diagramm pro Kategorie / Budget
- [ ] Import digitaler Kassenbons (bspw. Lidl oder Globus)
- [ ] (Weitere Meilensteine in Planung)

See the [open issues](https://github.com/github_username/repo_name/issues) for a full list of proposed features (and known issues).

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- CONTRIBUTING -->
## Contributing

Contributions are what make the open source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

### Entwickeln

Das Projekt kann lokal wie folgt aufgesetzt werden:

1. Repo klonen
   ```sh
   git clone https://github.com/tpoerschke/mokka-budget.git
   ```
2. Starten

   VS Code: `mvn clean javafx:run` oder `mvn clean javafx:run@debug` und per Visual Studio Code attachen (`.vscode/launch.json`)

   IntelliJ: Run Configuration `Launch`

3. Los entwickeln :)

### Bauen

Mithilfe des Shell-Skripts `build_app.sh` kann die Applikation für das vorliegende Betriebssystem gebaut werden. Unterstützt werden Windows, MacOS und Linux.

#### Windows

Um einen Windows-Installer lokal bauen zu können, werden folgende Packages vorausgesetzt:

- Wix-Toolset

#### Linux (RPM)

Um eine rpm-Datei lokal bauen zu können, werden folgende Packages vorausgesetzt:

- `rpmbuild`

## Your feature or enhancement

If you have a suggestion that would make this better, please fork the repo and create a pull request. You can also simply open an issue with the tag "enhancement".
Don't forget to give the project a star! Thanks again!

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- LICENSE -->
## License

Distributed under the GNU General Public License v3.0. See `LICENSE` for more information.

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- CONTACT -->
## Contact

Tim Poerschke - post@timkodiert.de

Project Link: [https://github.com/tpoerschke/mokka-budget](https://github.com/tpoerschke/mokka-budget)

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- ACKNOWLEDGMENTS -->
<!--
## Acknowledgments

* []()
* []()
* []()

<p align="right">(<a href="#readme-top">back to top</a>)</p>
-->


<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->

<!-- @formatter:off -->
[product-screenshot-1]: images/Screenshot-MonthlyOverview.png
[product-screenshot-2]: images/Screenshot-AnnualOverview.png
[product-screenshot-3]: images/Screenshot-AnalysisView.png

[OpenJDK-shield]: https://img.shields.io/badge/OpenJDK-222?style=for-the-badge&logo=OpenJDK
[OpenJDK-url]: https://adoptium.net/de/temurin
[Maven-shield]: https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=Apache%20Maven
[Maven-url]: https://maven.apache.org/
[Hibernate-shield]: https://img.shields.io/badge/Hibernate-59666C?style=for-the-badge&logo=Hibernate
[Hibernate-url]: https://hibernate.org/
[SQLite-shield]: https://img.shields.io/badge/SQLite-003B57?style=for-the-badge&logo=SQlite
[SQLite-url]: https://www.sqlite.org/index.html
[Lombok-shield]: https://img.shields.io/badge/lombok-d9230f?style=for-the-badge
[Lombok-url]: https://projectlombok.org/
[Dagger-shield]: https://img.shields.io/badge/Dagger-2196F3?style=for-the-badge
[Dagger-url]: https://dagger.dev/
[SonarQube-shield]: https://img.shields.io/badge/SonarQube-222?style=for-the-badge&logo=SonarCloud
[SonarQube-url]:https://sonarcloud.io/
<!-- @formatter:on -->