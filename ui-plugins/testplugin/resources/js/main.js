
function initTEST() {
	var project = rundeckPage.project();
	var para = document.createElement("div");
	
	para.style.fontSize = "150%";
	para.style.textAlign = "center";
	para.style.marginBottom = "10px";

	var newdiv = document.createElement("div");
	newdiv.id = "newfield";
	para.appendChild(newdiv);

	var x = jQuery(".main-panel .content").prepend(para);
	console.log(rundeckPage.path());
	if(rundeckPage.path() === "menu/projectHome"){
		//on any project page
	console.log("handler fired!");
	}
};

function url_path(baseUrl) {
    if (baseUrl.indexOf('/') == 0) {
        return baseUrl;
    }
    if (baseUrl.toLowerCase().indexOf('http') == 0) {
        var len = baseUrl.indexOf('://');
        if (len > 0) {
            var absurl = baseUrl.substring(len + 3);
            if (absurl.indexOf('/') >= 0) {
                absurl = absurl.substring(absurl.indexOf('/'));
                return absurl;
            } else {
                return '';
            }
        }
    }
}