# Project1-WAFFLES

WAFFLES, which stands for "Warwick’s Amazing Fast Food Logistic Engagement Service". WAFFLES is a web application for hosting restaurant information, customer reviews and customer favourites.

# HOW TO RUN

### Clone this repo to your local

`git clone https://github.com/YW-2022/PROJECT1.git` 

### CD to this repo

`cd WAFFLES` 

### Run

Use `build.sh` for **Linux/macOS** and use `run.bat` if you are on **Windows**.

We named the scripts different to make it easier to autocomplete in the **Linux/macOS** terminal,so when you want to run the script type `./b` and then press tab, it will expand it to `./build.sh`.

For **Windows**, the script name short instead. The tab auto-complete also works on this OS but in Command Prompt it clashes with `Report.md`,it does not clash in PowerShell. In the Command Prompt case, it is easier to type out the 3 characters.

To make it executable on **Linux/macOS**:

`chmod +x build.sh`

Then you should be able to run `-h` to see the script’s documentation:

`./build.sh -h`

In Windows, double click the `.bat` file and it should open up a Command Prompt window with the script’s documentation. Alternatively, in Command Prompt:

`run -h`

And in **Windows** PowerShell the syntax is:

`.\run.bat -h`

**Website**

To run the WAFFLES website on **Linux/macOS**:

`./build.sh -r`

This will compile your source code and use it for the WAFFLES website. The website will then be run on port **8080**. You can access it by going to http://localhost:8080/ on your preferred web browser.

In **Windows** Command Prompt, the commands are respectively:

`run -r` or `run -r 9090`

In **Windows** PowerShell, the commands are respectively:

`.\run.bat -r` or `.\run.bat -r 9090`

Finally, if you wish to run the initial bare-bones website, which does not use your code:

`java -jar waffles.jar`
