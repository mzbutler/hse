/* 
 * Archivo javascript para aplicacion de emocione en youtube clien side
 */


$(document).ready(load_Controls);

var params = {allowScriptAccess: "always"};
var atts = {id: "myytplayer"};
var currentVideo;
var childEmotionOpenn = false;


function load_Controls() {
    currentVideo = $('input#correntVideo').val();
    if (currentVideo === null) {
        currentVideo = "https://www.youtube.com/v/nKIu9yen5nc?enablejsapi=1&playerapiid=ytplayer&version=3";
    }
    swfobject.embedSWF(currentVideo,
            "ytapiplayer", "425", "356", "8", null, null, params, atts);
    loadRowsToUpdate();//load emotions when edit

}



function loadVideo() {
    var link = $('#currentVidUrl input').text().split("?v=");
    var video_id = $.trim(link[1]);
    var time = $('#currentVidTimeSeconds input').text();
    if (time.length > 1) {
        changeVideo(video_id, time);
    } else {
        changeVideo(video_id, 0);
    }
}

function selectVideo(row) {
    //load edit panel
    prepareEdit(row);
    //load video
    var link = $(row).find('#url_row').text().split("?v=");
    var time = $(row).find('#video_time').text();
    var video_id = $.trim(link[1]);
    console.log(video_id + ":" + time);
    changeVideo(video_id, time);
}

function selectSubtitle(row) {
    //load edit panel
    //load video
    var time = $(row).find('#data_time').text();
    ytplayer = document.getElementById("myytplayer");
    ytplayer.seekTo(time, false);
    ytplayer.pauseVideo();
}

function changeVideoUrl(url) {
    $('#infoBox').css({"display": "inline"});
    console.log(url);
    ytplayer = document.getElementById("myytplayer");
    ytplayer.addEventListener("onStateChange", "onytplayerStateChange");
    ytplayer = document.getElementById("myytplayer");
    var link = url.split("?v=");
    var video_id = $.trim(link[1]);
    ytplayer.loadVideoById(video_id);
    ytplayer.pauseVideo();
}


function prepareEdit(row) {
    var id_video = $.trim($(row).find('#id_video').text());
    var link = $.trim($(row).find('#url_row').text());
    var scene = $.trim($(row).find('#scene_row').text());
    var person = $.trim($(row).find('#person_row').text());
    var time = $.trim($(row).find('#time_row').text());
    var emotion = $.trim($(row).find('#emotion_row').text());
    $('#currentVidId input').val(id_video);
    $('#currentVidUrl input').val(link);
    $('#currentVidScene input').val(scene);
    $('#currentVidPerson input').val(person);
    $('#currentVidTime input').val(time);
    var selectInput = $('#currentVidEmotion select option').each(function() {
        var selectedEmotion = $(this).text();
        console.log($(this).text() + ":" + emotion);
        if (selectedEmotion.toString() === emotion) {
            console.log("here is!!!!");
            $(this).attr("selected", true);
        } else {
            console.log("desmarcado:" + selectedEmotion);
            $(this).removeAttr("selected");
        }
    });
    console.log(selectInput);
    // $('#currentVidEmotion select').val(emotion);
}

function closeMessage(action) {
    console.log(action);
    $('#editor').css({"display": "inline-block"});
    $('#mesaggeInfoPanel').css({"display": "none"});
    $('#infoBox').css({"display": "none"});

}

function cancelActionJS(action) {
    console.log(action);
    $('#infoBox').css({"display": "none"});
    var cholds = $('#action_options').children();
    var child = cholds[2];
    $(child).click();

}


function applyActionJS() {
    $('#infoBox').css({"display": "none"});
    var cholds = $('#action_options').children();
    var child = cholds[0];
    $(child).click();
}

function  onChngeVideoUrl(text) {
    if (text !== null) {
        $('#ytPlayerButtons').css({"display": "block"});
    } else {
        $('#ytPlayerButtons').css({"display": "none"});
    }

}

function captureEscene() {
    //obtain video data
    if (childEmotionOpenn) {
        alert('You must apply the open row before continue.');
    } else {
        childEmotionOpenn = true;
        ytplayer = document.getElementById("myytplayer");
        ytplayer.pauseVideo();
        var timeSeconds = Math.floor(ytplayer.getCurrentTime());
        var maskedTime = formatTime(timeSeconds);
        var videoId = $('#currentVidId input').val();
        var videoUrl = $('#currentVidUrl input').val();
        //alert(timeSeconds + "|" + maskedTime+ "|" +videoId + "|" +videoUrl);

        var count = $("#scenesPanel").children().length;
        //build html    
        //scene
        var sceneID;
        if (count > 0) {
            var rowsPanel = document.getElementById('scenesPanel');
            var rows = $(rowsPanel).children()[count - 1];
            var timeColum = $(rows).children()[4];
            var input = $(timeColum).children()[0];
            console.log(input.value);

            var sceneColum = $(rows).children()[1];
            var inputS = $(sceneColum).children()[0];

            if (input.value === maskedTime) {
                sceneID = inputS.value;
            } else {
                var nextSeq = parseInt(inputS.value.split("_")[1]);
                nextSeq = nextSeq + 1;
                sceneID = "escene_" + nextSeq;
            }
        } else {
            sceneID = "escene_" + 1;
        }


        var columrow = document.createElement("div");
        if (count % 2 === 0) {
            columrow.className = "rowResultA";
        } else {
            columrow.className = "rowResultB";
        }

        //ops
        var opsdiv = document.createElement("div");
        opsdiv.className = "columResultAP";
        var applyRow = document.createElement("img");
        applyRow.className = "imgCommand";
        applyRow.src = "img/apply.png";
        applyRow.onclick = applyEmotionRow;
        opsdiv.appendChild(applyRow);

        var delRow = document.createElement("img");
        delRow.className = "imgCommand";
        delRow.src = "img/delete.png";
        opsdiv.appendChild(delRow);
        delRow.onclick = deleteEmotionRow;

        var updateRow = document.createElement("img");
        updateRow.className = "imgCommand";
        updateRow.src = "img/edit.png";
        updateRow.style.display = "none";
        updateRow.onclick = editEmotionRow;
        opsdiv.appendChild(updateRow);
        columrow.appendChild(opsdiv);

        var scenediv = document.createElement("div");
        scenediv.className = "columResultAP";
        var sceneText = document.createElement("input");
        scenediv.appendChild(sceneText);
        sceneText.style.width = "90%";
        sceneText.value = sceneID;
        sceneText.disabled = true;
        columrow.appendChild(scenediv);


        //person    
        var persondiv = document.createElement("div");
        persondiv.className = "columResultAP";
        var personText = document.createElement("input");
        persondiv.appendChild(personText);
        personText.style.width = "90%";
        columrow.appendChild(persondiv);

        //var combo = document.getElementById("currentVidEmotion");
        //person    
        var combodiv = document.createElement("div");
        combodiv.className = "columResultAP";
        var selectControl = document.createElement("select");
        var arayOpts = document.getElementById('currentVidEmotion');
        var select = $(arayOpts).children()[0];

        $(select).children().each(function(i) {
            var option = document.createElement("option");
            option.value = select[i].value;
            option.text = select[i].text;
            selectControl.appendChild(option);
        });
        combodiv.appendChild(selectControl);
        columrow.appendChild(combodiv);

        /*var comboDiv =  document.createElement( "div" );
         comboDiv.className = "columResultAP";  
         var conmboInput = document.createElement( "select" ); */




        //masked time    
        var timediv = document.createElement("div");
        timediv.className = "columResultAP";
        var timeText = document.createElement("input");
        timeText.value = maskedTime;
        timediv.appendChild(timeText);
        timeText.disabled = true;
        timeText.style.width = "90%";
        columrow.appendChild(timediv);

        //timeSec    
        var timeSecdiv = document.createElement("div");
        timeSecdiv.className = "columResultAP";
        timeSecdiv.style.display = "none";
        var timeSecText = document.createElement("input");
        timeSecText.value = timeSeconds;
        timeSecdiv.appendChild(timeSecText);
        timeSecText.disabled = true;

        timeSecText.style.width = "90%";
        columrow.appendChild(timeSecdiv);

        var vidIddiv = document.createElement("div");
        vidIddiv.className = "columResultAP";
        vidIddiv.style.display = "none";
        var vidIdText = document.createElement("input");
        vidIddiv.appendChild(vidIdText);
        vidIdText.disabled = true;
        vidIdText.value = videoId;

        columrow.appendChild(vidIddiv);

        var vidUrldiv = document.createElement("div");
        vidUrldiv.className = "columResultAP";
        vidUrldiv.style.display = "none";
        var vidUrlText = document.createElement("input");
        vidUrldiv.appendChild(vidUrlText);
        vidUrlText.disabled = true;
        vidUrlText.value = videoUrl;

        columrow.appendChild(vidUrldiv);

        document.getElementById("scenesPanel").appendChild(columrow);
    }


}

function deleteEmotionRow(event) {
    var rowControls = event.target.parentNode.parentNode;
    $(rowControls).remove();
    childEmotionOpenn = false;
}

function editEmotionRow(event) {
    if (!childEmotionOpenn) {
        event.target.style.display = "none";
        var rowControls = event.target.parentNode;
        var applyButton = $(rowControls).children()[0];
        applyButton.style.display = "inline-block";

        var row = event.target.parentNode.parentNode;
        for (var x = 2; x < 4; x++) {
            var column = $(row).children()[x];
            var input = $(column).children()[0];
            $(input).attr('disabled', false);
        }
        childEmotionOpenn = true;
    } else {
        alert('You must apply the open row before continue.');
    }


}

function applyEmotionRow(event) {
    var row = event.target.parentNode.parentNode;
    for (var x = 1; x < 4; x++) {
        var column = $(row).children()[x];
        var input = $(column).children()[0];
        if ($(input).val() === "") {
            childEmotionOpenn = true;
            alert("All fields are requiered!");
            break;
        } else {
            childEmotionOpenn = false;

        }
    }

    if (!childEmotionOpenn) {
        for (var x = 2; x < 4; x++) {
            var column = $(row).children()[x];
            var input = $(column).children()[0];
            $(input).attr('disabled', 'disabled');
        }
        event.target.style.display = "none";
        var rowControls = event.target.parentNode;
        var editButton = $(rowControls).children()[2];
        editButton.style.display = "inline-block";
    }

}

function formatTime(time) {
    var realTime;
    if (time >= 3600) {
        var hours = time / 3600;
        var realmin = time / 60;
        if (realmin >= 60) {
            var mins = realmin - 60;
            var secs = time % 60;

            if (secs < 9) {
                secs = "0" + Math.floor(secs);
            } else {
                secs = Math.floor(secs);
            }
            if (mins < 9) {
                mins = "0" + Math.floor(mins);
            } else {
                mins = Math.floor(mins);
            }
            realTime = Math.floor(hours) + ":" + mins + ":" + secs;
        } else {
            if (realmin < 9) {
                realmin = "0" + Math.floor(realmin);
            } else {
                realmin = Math.floor(realmin);
            }
            realTime = Math.floor(hours) + ":" + realmin + ":00";
        }
    } else {
        if (time >= 60) {
            var mins = time / 60;
            var secs = time % 60;
            if (secs < 9) {
                secs = "0" + Math.floor(secs);
            } else {
                secs = Math.floor(secs);
            }
            mins = Math.floor(mins);

            realTime = mins + ":" + secs;

        } else {
            if (time <= 9) {
                realTime = "0:0" + Math.floor(time);
            } else {
                realTime = "0:" + Math.floor(time);
            }

        }

    }
    return realTime;
}

function applyRecord(button) {
    if (childEmotionOpenn) {
        alert('You must apply the open row before continue.');
    } else {

        var arreglo = "";
        var emotioRows = document.getElementById("scenesPanel");
        $(emotioRows).children().each(function(i) {
            var row = $(emotioRows).children()[i];
            var registro = "";
            for (var x = 1; x < 8; x++) {
                var column = $(row).children()[x];
                var input = $(column).children()[0];
                registro = registro + $(input).val();
                if (x < 7) {
                    registro = registro + "|";
                }
            }
            arreglo = arreglo + registro + ",";
        });
        var divRow = document.getElementById("emotionRows");
        var inputRows = $(divRow).children()[0];
        inputRows.value = arreglo;


        var divContainer = button.parentNode;
        var editButton = $(divContainer).children()[1];
        $(editButton).click();
    }

}

function loadRowsToUpdate() {
    var divRow = document.getElementById("emotionRows");
    if (divRow !== undefined  & divRow!== null) {
        var inputRows = $(divRow).children()[0];
        if (inputRows !== undefined & inputRows.value !== "") {

            var fields = inputRows.value.split("|");
            for (var x = 0; x < fields.length; x++) {
                console.log(fields[x]);
            }
            var count = $("#scenesPanel").children().length;
            var columrow = document.createElement("div");
            if (count % 2 === 0) {
                columrow.className = "rowResultA";
            } else {
                columrow.className = "rowResultB";
            }
            //ops
            var opsdiv = document.createElement("div");
            opsdiv.className = "columResultAP";
            var applyRow = document.createElement("img");
            applyRow.className = "imgCommand";
            applyRow.src = "img/apply.png";
            applyRow.onclick = applyEmotionRow;
            applyRow.style.display = "none";
            opsdiv.appendChild(applyRow);

            var delRow = document.createElement("img");
            delRow.className = "imgCommand";
            delRow.src = "img/delete.png";
            opsdiv.appendChild(delRow);
            delRow.onclick = deleteEmotionRow;

            var updateRow = document.createElement("img");
            updateRow.className = "imgCommand";
            updateRow.src = "img/edit.png";
            updateRow.style.display = "inline";
            updateRow.onclick = editEmotionRow;
            opsdiv.appendChild(updateRow);
            columrow.appendChild(opsdiv);

            var scenediv = document.createElement("div");
            scenediv.className = "columResultAP";
            var sceneText = document.createElement("input");
            scenediv.appendChild(sceneText);
            sceneText.style.width = "90%";
            sceneText.value = fields[0];
            sceneText.disabled = true;
            columrow.appendChild(scenediv);


            //person    
            var persondiv = document.createElement("div");
            persondiv.className = "columResultAP";
            var personText = document.createElement("input");
            persondiv.appendChild(personText);
            personText.style.width = "90%";
            personText.value = fields[1];
            personText.disabled = true;
            columrow.appendChild(persondiv);

            //var combo = document.getElementById("currentVidEmotion");
            //person    
            var combodiv = document.createElement("div");
            combodiv.className = "columResultAP";
            var selectControl = document.createElement("select");
            var arayOpts = document.getElementById('currentVidEmotion');
            var select = $(arayOpts).children()[0];
            selectControl.disabled = true;

            $(select).children().each(function(i) {
                var option = document.createElement("option");
                option.value = select[i].value;
                option.text = select[i].text;
                selectControl.appendChild(option);
                if (option.value === fields[2]) {
                    option.selected = true;
                }
            });

            combodiv.appendChild(selectControl);
            columrow.appendChild(combodiv);


            /*var comboDiv =  document.createElement( "div" );
             comboDiv.className = "columResultAP";  
             var conmboInput = document.createElement( "select" ); */




            //masked time    
            var timediv = document.createElement("div");
            timediv.className = "columResultAP";
            var timeText = document.createElement("input");
            timediv.appendChild(timeText);
            timeText.disabled = true;
            timeText.style.width = "90%";
            timeText.value = fields[3];
            columrow.appendChild(timediv);

            //timeSec    
            var timeSecdiv = document.createElement("div");
            timeSecdiv.className = "columResultAP";
            timeSecdiv.style.display = "none";
            var timeSecText = document.createElement("input");
            timeSecText.value = fields[4];
            timeSecdiv.appendChild(timeSecText);
            timeSecText.disabled = true;

            timeSecText.style.width = "90%";
            columrow.appendChild(timeSecdiv);

            var vidIddiv = document.createElement("div");
            vidIddiv.className = "columResultAP";
            vidIddiv.style.display = "none";
            var vidIdText = document.createElement("input");
            vidIddiv.appendChild(vidIdText);
            vidIdText.disabled = true;
            vidIdText.value = fields[5];

            columrow.appendChild(vidIddiv);

            var vidUrldiv = document.createElement("div");
            vidUrldiv.className = "columResultAP";
            vidUrldiv.style.display = "none";
            var vidUrlText = document.createElement("input");
            vidUrldiv.appendChild(vidUrlText);
            vidUrlText.disabled = true;
            vidUrlText.value = fields[6];

            columrow.appendChild(vidUrldiv);

            document.getElementById("scenesPanel").appendChild(columrow);
        }
    }
}
//////////////video controls/////////////////

function onYouTubePlayerReady(playerId) {
    ytplayer = document.getElementById("myytplayer");
    ytplayer.addEventListener("onStateChange", "onytplayerStateChange");
    console.log("Ready....");
}

function onytplayerStateChange(newState) {
    console.log("Player's new state: " + newState);
    ytplayer = document.getElementById("myytplayer");
    //ytplayer.pauseVideo();
}

function onStateChange() {
    console.log("new state: " + newState);
}

function changeVideo(video_id, time) {
    ytplayer = document.getElementById("myytplayer");
    ytplayer.addEventListener("onStateChange", "onytplayerStateChange");
    ytplayer = document.getElementById("myytplayer");
    ytplayer.loadVideoById(video_id, time, 'medium');
    ytplayer.pauseVideo();
}

function playVideo() {
    ytplayer = document.getElementById("myytplayer");
    ytplayer.playVideo();
}


