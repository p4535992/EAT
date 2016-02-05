##############################################################
### SIMPLE JAVA PROGRAMM FOR EXTRACT INFORMATION FROM WEB PAGE###
##############################################################
#########################
###Last Update: 2016-01-28
#########################
A simple java project where i use GATE (https://gate.ac.uk/) with a my specific library 
gate-basic (https://github.com/p4535992/gate-basic) for analize web document and extract specific information.

[![Release](https://img.shields.io/github/release/p4535992/ExtractInfo.svg?label=maven)](https://jitpack.io/p4535992/ExtractInfo)

Example extraction code 1(version 1.6.X):
```java
public List<GeoDocument> extractInfoSingleURL(
        String driverDatabase, String dialectDatabase, String hostDatabase, String portDatabase, String user,
        String pass, String dbOutput, String dbInput, String tableOutput, String tableInput,
        String columnTableInput, String limit, String offset, boolean createNewGeodocumentTable, boolean erase) {

    List<GeoDocument> listGeo = new ArrayList<>();
    ExtractInfoWeb web = prepareListAndGATE(
            driverDatabase, dialectDatabase, hostDatabase, portDatabase, user,
            pass, dbOutput, dbInput, tableOutput, tableInput,
            columnTableInput, limit, offset);
    try {
        logger.info("RUN PROCESS 1: Abilitate for each single url");
        if (_listUrl.isEmpty()) {
            logger.info("The list of urls you get from the table:" + tableInput +
                    " from the columns " + columnTableInput + " empty!!!");
        } else {
            logger.info("Loaded a list of: " + _listUrl.size() + " files");
            GeoDocument geoDoc;
            for (URL url : _listUrl) {
                geoDoc = web.ExtractGeoDocumentFromUrl(
                        url, tableOutput, tableOutput, createNewGeodocumentTable, erase);
                if (geoDoc != null) listGeo.add(geoDoc);
            }
        }
    } catch (OutOfMemoryError e) {
        logger.error("java.lang.OutOfMemoryError, Reload the programm please");
    }
    return listGeo;
}

public List<GeoDocument> extractInfoFile(
            String directoryFiles, String driverDatabase, String dialectDatabase, String hostDatabase, String portDatabase, String user,
            String pass, String dbOutput, String tableOutput, String offset, String limit, boolean createNewGeodocumentTable, boolean erase) {

    List<GeoDocument> listGeo = new ArrayList<>();
    try {
        logger.info("RUN PROCESS 3: Abilitate for single file or for a directory");
        //String DIRECTORY_FILE = "C:\\Users\\Marco\\Downloads\\parseWebUrls";
        ExtractInfoWeb web = prepareListAndGATE(directoryFiles, driverDatabase, dialectDatabase, hostDatabase, portDatabase, user,
                pass, dbOutput, tableOutput, offset, limit);
        if (_listFile.isEmpty()) {
       /* logger.info("The list of urls you get from the table:" + TABLE_INPUT +
                " from the columns " + COLUMN_TABLE_INPUT + " empty!!!");*/
            logger.warn("The list of files you get is empty!!");
        } else {
            logger.info("Loaded a list of: " + _listFile.size() + " files");
            web.ExtractGeoDocumentFromListFiles(
                    _listFile, tableOutput, tableOutput, createNewGeodocumentTable, erase);
        }
        logger.info("Obtained a list of: " + listGeo.size() + " GeoDocument");
    } catch (OutOfMemoryError e) {
        logger.error("java.lang.OutOfMemoryError, Reload the programm please");
    }
    return listGeo;
}
```

You can the dependency to this github repository With jitpack (https://jitpack.io/):

<!-- Put the Maven coordinates in your HTML: -->
 <pre class="prettyprint">&lt;dependency&gt;
  &lt;groupId&gt;com.github.p4535992&lt;/groupId&gt;
  &lt;artifactId&gt;ExtractInfo&lt;/artifactId&gt;
  &lt;version&gt;<span id="latest_release">1.6.8</span>&lt;/version&gt;
&lt;/dependency&gt;  </pre>

<!-- Add this script to update "latest_release" span to latest version -->
<script>
      var user = 'p4535992'; // Replace with your user/repo
      var repo = 'ExtractInfo'

      var xmlhttp = new XMLHttpRequest();
      xmlhttp.onreadystatechange = function() {
          if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
              var myArr = JSON.parse(xmlhttp.responseText);
              populateRelease(myArr);
          }
      }
      xmlhttp.open("GET", "https://api.github.com/repos/" user + "/" + repo + "/releases", true);
      xmlhttp.send();

      function populateRelease(arr) {
          var release = arr[0].tag_name;
          document.getElementById("latest_release").innerHTML = release;
      }
</script>
