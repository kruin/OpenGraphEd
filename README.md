# GraphEd-JGraphEd

Legacy Java graph editor (2004) restored to run on modern Java.

Download the latest binary:
https://github.com/<username>/GraphEd-JGraphEd/releases

![GraphEd Screenshot](docs/screenshot.png)

---

## Run

Download the latest binary from the **Releases** page (`JGraphEd.jar`).

Then run:

```
java -jar JGraphEd.jar
```

---

## Build

Windows:

```
build.bat
```

Run locally:

```
run.bat
```

---

## Structure

```
dataStructure/      core data structures
graphStructure/     graph model
operation/          graph algorithms
userInterface/      Swing UI
images/             toolbar icons
help/               HTML help pages
docs/               PDF files from around 2004
```

---

## Notes

The original code used several obsolete Java APIs.
The following updates were applied:

* replaced legacy JPEG encoder with ImageIO
* renamed identifiers conflicting with modern Java keywords
* added simple build and run scripts
* included runtime resources in build output

Applet support remains for historical reasons but modern browsers do not support Java applets.
The desktop application is the primary runtime target.
