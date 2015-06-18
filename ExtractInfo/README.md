##############################################################
### SIMPLE JAVA PROGRAMM FOR EXTRACT INFORMATION FROM WEB PAGE###
##############################################################

[![Release](https://img.shields.io/github/release/p4535992/ExtractInfo.svg?label=maven)](https://jitpack.io/p4535992/ExtractInfo)

You can the dependency to this github repository With jitpack (https://jitpack.io/):

<!-- Put the Maven coordinates in your HTML: -->
 <pre class="prettyprint">&lt;dependency&gt;
  &lt;groupId&gt;com.github.p4535992&lt;/groupId&gt;
  &lt;artifactId&gt;ExtractInfo&lt;/artifactId&gt;
  &lt;version&gt;<span id="latest_release">1.1</span>&lt;/version&gt;
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
