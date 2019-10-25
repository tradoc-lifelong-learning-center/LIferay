(function(){
  var audioLinks = document.querySelectorAll('[data-podcastlink]');

  for(var i = 0; i<audioLinks.length; i++){
    audioLinks[i].onclick = newTabFunction;
  }


  function newTabFunction(e) {
    var linkId = e.target.dataset.podcastlink;
    if(!linkId) return false;

    var html = document.querySelector("[data-podcastaudio=" + linkId + "]").outerHTML;
    html = html.replace("<audio ", "<audio autoplay ")

    if(!html) return false;

    var w = window.open();
    w.document.body.innerHTML = html;

    var audio = w.document.getElementById(linkId);
    //audio.setAttribute("autoplay","")
    //console.log(audio)

    /*var script = w.document.createElement("script");
    var text = w.document.createTextNode("document.getElementById(linkId).play();");
    script.appendChild(text);
    w.body.appendChild(script);*/

  /*console.log(w.document.getElementById(linkId))
  w.document.getElementById(linkId).play()*/
//w.document.getElementById(linkId).play()


  var src = audio.firstElementChild.getAttribute('src');
  audio.load();

  fetchAudioAndPlay(src);

  }

  function fetchAudioAndPlay(src) {
      fetch(src)
      .then(response => response.blob())
      .then(blob => {
        audio.srcObject = blob;
        return audio.play();
      })
      .then(_ => {
        // Video playback started ;)
      })
      .catch(e => {
        // Video playback failed ;(
      })
    }

})();
