<!-- Improved compatibility of back to top link: See: https://github.com/othneildrew/Best-README-Template/pull/73 -->
<a name="readme-top"></a>

**Language:** English · [Deutsch](README.de.md)

<!-- PROJECT SHIELDS -->
<!--
https://www.markdownguide.org/basic-syntax/#reference-style-links
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
    Monitoring, Organization, Control, Categorization & Analysis – The household budget app for full financial control!
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
      <a href="#about-the-project">About the Project</a>
      <ul>
        <li><a href="#screenshots">Screenshots</a></li>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#installation">Installation</a>
    </li>
    <li><a href="#roadmap">Roadmap</a></li>
    <li>
        <a href="#contributing">Contributing</a>
        <ul>
            <li><a href="#development">Development</a></li>
            <li><a href="#building">Building</a></li>
            <li><a href="#your-feature-or-enhancement">Your feature or enhancement</a></li>
        </ul>
    </li>
    <li><a href="#license">License</a></li>
    <li><a href="#contact">Contact</a></li>
    <!--<li><a href="#acknowledgments">Acknowledgments</a></li>-->
  </ol>
</details>



<!-- ABOUT THE PROJECT -->

## About the Project

[![MOKKA-Budget-MainView][product-screenshot-1]](https://github.com/tpoerschke/mokka-budget)

With so many subscriptions and widespread card payments, it is easy to lose track of your spending.
This household budget app helps you stay on top of things by letting you track – via import or manual entry –, plan, and analyze your expenses.

Features:
- **Planning** of recurring expenses (and income) (**fixed costs**)
- **Tracking of actual expenses** (and income) through import or manual entry
- **Categorization** of expenses (and income)
- Management of **budgets per category**
- **Analysis** showing the development of an expense category over time

And the best part: **No cloud** or data-hungry services. **Your data** is processed locally **on your computer** and stored **encrypted**.
Encryption is available from version 1.1.0 and can be disabled.

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

The application is available for download with the latest release: https://github.com/tpoerschke/mokka-budget/releases

Instructions for Mac:

1. Download and extract the latest release (.dmg)
2. Move the app to your Applications folder
3. Launch the app

Note: You may need to explicitly allow execution if macOS reports that it cannot (yet) verify the application for malware or similar.
In System Settings (System Settings > Privacy & Security), you can allow the app to run after you have attempted to launch it once.

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- ROADMAP -->
## Roadmap

- [x] Monthly overview
- [x] Annual overview
- [x] Transaction management
- [x] Category system
- [x] Transaction import
- [x] Budgets
- [x] Basic analysis (bar chart per category)
- [ ] Import of physical receipts (e.g. via OCR)
- [ ] Import of digital receipts (e.g. Lidl or Globus)
- [ ] Burn-up chart per category / budget
- [ ] (Further milestones in planning)

See the [open issues](https://github.com/github_username/repo_name/issues) for a full list of proposed features (and known issues).

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- CONTRIBUTING -->
## Contributing

Contributions are what make the open source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

### Development

You can set up the project locally as follows:

1. Clone the repo
   ```sh
   git clone https://github.com/tpoerschke/mokka-budget.git
   ```
2. Start the app

   VS Code: `mvn clean javafx:run` or `mvn clean javafx:run@debug` and attach via Visual Studio Code (`.vscode/launch.json`)

   IntelliJ: Run configuration `Launch`

3. Start developing :)

### Building

Use the shell script `build_app.sh` to build the application for your current operating system. Windows, macOS, and Linux are supported.

#### Windows

The following packages are required to build a Windows installer locally:

- Wix Toolset

#### Linux (RPM)

The following packages are required to build an RPM file locally:

- `rpmbuild`

### Your feature or enhancement

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
