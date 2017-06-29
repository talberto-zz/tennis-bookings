/*
 * Le code ci dessous est Indigo tous droits réservés 2007-2013
 *
 */

var g_idg_adsl_bis;
/*
document.onselectstart=function(){return false;};
document.ondragstart=function(){return false;};
function dom_noselect(e)
{
	if (navigator.userAgent.indexOf("MSIE") >= 0)
		return;
	var t;
	var tn;
	if(e.srcElement)
		t = e.srcElement;
	else if(e.target)
		t = e.target;
	if (t.nodeType == 3)
		t = t.parentNode;
	tn=t.tagName;
	if(tn=='INPUT' || tn=='SELECT' || tn=='TEXTAREA')
		return true;
	else
		return false;
}
function dom_oklink()
{
	return true;
}
function dom_cb()
{
	window.event.cancelBubble=true;
}
document.onclick=dom_oklink;
document.onmousedown=dom_noselect;
*/
function dom_cb()
{
   return true;
}

var box_ms; // Current box to set to
var box_tm; // Timer to stop moving process
function box_clr()
{
   window.clearTimeout(box_tm);
}
function box_end()
{
   box_clr();
   box_ms=0;
}
box_tm=setTimeout('box_end()',100);
function box_bgn(n,box,cs,cu)
{
   var obj=document.getElementById(n);
   var s=obj.value;
   if(s=='-1') return;
   box_clr();
   if(s=='0') {
      box_ms=1;
   }
   else {
      box_ms=-1;
   }
   if(box_ms>0){
      obj.value='1';
      box.style.backgroundColor=cs;
   }
   if(box_ms<0){
      obj.value='0';
      box.style.backgroundColor=cu;
   }
}
function box_out()
{
   box_tm=setTimeout('box_end()',500);
}
function box_mm(n,box,cs,cu)
{
   var obj=document.getElementById(n);
   if(obj.value=='-1'){
      return;
   }
   box_clr();
   if(box_ms>0){
      obj.value='1';
      box.style.backgroundColor=cs;
   }
   if(box_ms<0){
      obj.value='0';
      box.style.backgroundColor=cu;
   }
}


function updatebtn(obj,evt,opt)
{
   return;
   switch(evt)
   {
      case 1:
         obj.className = 'boutonfocus' + String(opt);
         break;
      default:
         obj.className = 'bouton' + String(opt);
   }
}
function fs()
{
   if(document.getElementById('workpage')) document.getElementById('workpage').style.display='none';
   if(document.getElementById('waitpage')) document.getElementById('waitpage').style.display='block';
   if(document.getElementById('waitlogo')) document.getElementById('waitlogo').style.display='block';
   g_idg_submit=true;
   setTimeout("document.forms[0].submit()",10);
}
function fsh(idact)
{
   f=window.open('about:blank','ics_instant_msg','width=150,height=150,left='+screen.width+',top=0');
   document.forms[0].idact.value=idact;
   document.forms[0].target='ics_instant_msg';
   g_idg_submit=true;
   document.forms[0].submit();
   window.focus();
   document.forms[0].target='_self';
}
function fspop(idact,w,h)
{
   var opt = '';
   opt = 'width='+w+',height='+h+',left=0,top=0';
   f=window.open('about:blank','idg_sub',opt);
   f.name='idg_sub';
   document.forms[0].idact.value=idact;
   document.forms[0].target='idg_sub';
   g_idg_submit=true;
   document.forms[0].submit();
   self.document.forms[0].target='_self';
}
function fspopscroll(idact,w,h)
{
   var opt = '';
   opt = 'scrollbars=yes,width='+w+',height='+h+',left=0,top=0';
   f=window.open('about:blank','idg_sub',opt);
   f.name='idg_sub';
   document.forms[0].idact.value=idact;
   document.forms[0].target='idg_sub';
   g_idg_submit=true;
   document.forms[0].submit();
   self.document.forms[0].target='_self';
}
function MM_swapImgRestore()
{ //v3.0
   var i,x,a=document.MM_sr;
   for(i=0;a&&i<a.length&&(x=a[i])&&x.oSrc;i++) x.src=x.oSrc;
}
function MM_preloadImages()
{ //v3.0
   var d=document;
   if(d.images){
      if(!d.MM_p) d.MM_p=new Array();
      var i,j=d.MM_p.length,a=MM_preloadImages.arguments;
      for(i=0; i<a.length; i++)
         if (a[i].indexOf("#")!=0){
            d.MM_p[j]=new Image;
            d.MM_p[j++].src=a[i];
         }
      }
}
function MM_findObj(n, d)
{ //v4.01
   var p,i,x;
   if(!d) d=document;
   if((p=n.indexOf("?"))>0&&parent.frames.length) {
      d=parent.frames[n.substring(p+1)].document;
      n=n.substring(0,p);
   }
   if(!(x=d[n])&&d.all) x=d.all[n];
   for (i=0;!x&&i<d.forms.length;i++) x=d.forms[i][n];
   for(i=0;!x&&d.layers&&i<d.layers.length;i++) x=MM_findObj(n,d.layers[i].document);
   if(!x && d.getElementById) x=d.getElementById(n);
   return x;
}
function MM_swapImage()
{ //v3.0
   var i,j=0,x,a=MM_swapImage.arguments;
   document.MM_sr=new Array;
   for(i=0;i<(a.length-2);i+=3)
      if ((x=MM_findObj(a[i]))!=null){
         document.MM_sr[j++]=x;
         if(!x.oSrc) x.oSrc=x.src;
         x.src=a[i+2];
      }
}
function swapStyle(style_a,style_b,obj)
{
   o=document.getElementById(obj);
   if(o.className == style_b)
   {
      o.className=style_a;
   }
   else
   {
      o.className=style_b;
   }
}
var ManageTabSelectedBlock='';
var ManageTabSelectedTab='';
function ManageTab(tabname,blockname,focused,selected)
{
   if(document.getElementById(blockname).style.display=='none')
   {
      if(focused)
      {
         document.getElementById(tabname).className = 'tab_unselectedfocused';
      }
      else
      {
         document.getElementById(tabname).className = 'tab_unselectedunfocused';
      }
      if(selected)
      {
         if(ManageTabSelectedBlock!='')
         {
            document.getElementById(ManageTabSelectedBlock).style.display='none';
            document.getElementById(ManageTabSelectedTab).className = 'tab_unselectedunfocused';
         }
         document.getElementById(blockname).style.display='block';
         document.getElementById(tabname).className = 'tab_selectedunfocused';
         ManageTabSelectedBlock = blockname;
         ManageTabSelectedTab = tabname;
         if(document.forms[0].idtab) document.forms[0].idtab.value = tabname;
      }
   }
   else
   {
      if(focused)
      {
         document.getElementById(tabname).className = 'tab_selectedfocused';
      }
      else
      {
         document.getElementById(tabname).className = 'tab_selectedunfocused';
      }
   }
   return true;
}

function removeSelectOpt(selId)
{
   var sel = document.getElementById(selId);
   for (var i=0; i<sel.options.length; i++)
   {
      var opt = sel.options[i].selected = "";
   }
}

function popup(popupid,x,y)
{
   if(document.getElementById(popupid))
   {
      if(x!=0 || y!=0)
      {
         document.getElementById(popupid).style.top=y;
         document.getElementById(popupid).style.left=x;
         document.getElementById(popupid).style.visibility='visible';
      }
      else
      {
         document.getElementById(popupid).style.visibility='hidden';
      }
   }
   window.status='';
}

function mouse(e)
{
   e=e||window.event;
   if(e.pageX||e.pageY)
      return {
         x:e.pageX,
         y:e.pageY
         };
   else
      return {
         x:e.clientX+document.body.scrollLeft-document.body.clientLeft,
         y:e.clientY+document.body.scrollTop-document.body.clientTop
         };
}

function button(e)
{
   e=e||window.event;
   if(e.button)
      return e.button;
   if(e.wich)
      return e.wich;
}
/*
function getposition(obj)
{
   var left=0;
   var top=0;
   while(obj.offsetParent){
      left+=obj.offsetLeft;
      top+=obj.offsetTop;
      obj=obj.offsetParent;
   }
   left+=obj.offsetLeft;
   top+=obj.offsetTop;
   return {
      x:left,
      y:top
   };
}
*/
var idg_jqm_selected;
function idg_jqmShow(obj)
{
   idg_jqm_selected=$("select,input,radio").not("jqmWindow *");
   idg_jqm_selected.css("visibility","hidden");
   $(obj).jqmShow();
}

function idg_jqmHide(obj)
{
   $(obj).jqmHide();
   idg_jqm_selected.css("visibility","visible");
}

function idg_button(obj)
{
/*
   var cls = obj.attr("class");
   if(cls!="")
   {
      if(cls!="bouton_menu" && cls!="bouton")
      {
         obj.removeClass(cls);
         //var obj2 = obj.children(".ui-button-text");
         var html = obj.html();
         obj.button({
            icons:{
               primary:cls
            }
         });
      if( (html=="&nbsp;") || (html=="") || (html==" ") )
      {
         var obj2 = obj.find(".ui-button-text");
         obj2.html("").css("width","26px").css("height","26px").css("padding","0");
      }
   }
   else
   {
      obj.button();
   }
}
*/
}
function idg_chk_opt(val)
{
   var opt = $('#idgopt').val();
   return (opt.indexOf(val)>=0);
}
function idg_ui_layer(obj)
{
   if(idg_chk_opt("ui_layer"))
   {
      var objset = obj.find("select[size='1']").not('.no_ui_layer').not('[multiple]');
      objset.not('.ui-fixed-width').addClass("idg_ui_layer").selectmenu({
         style:'dropdown',
         width:"auto",
         menuWidth:"auto"
      });
      objset.not('.idg_ui_layer').each(function(){$(this).addClass("idg_ui_layer").selectmenu({
         style:'dropdown',
         width:$(this).width(),
         menuWidth:"auto"
      })});
   //obj.find("input[type=checkbox]").buttonset();
   }
}

function idg_select_set(obj,val)
{
   if(idg_chk_opt("ui_layer"))
   {
      $(obj).selectmenu('value',val);
   }
   else
      obj.value=val;
}

// ckeditor helper
var editor;
function createEditor()
{
   if ( editor )
      return;

   var html = document.getElementById( 'editorcontents' ).innerHTML;

   // Create a new editor inside the <div id="editor">
   editor = CKEDITOR.appendTo( 'editor' );
   editor.setData( html );

// This sample may break here if the ckeditor_basic.js is used. In such case, the following code should be used instead:
/*
	if ( editor.setData )
		editor.setData( html );
	else
		CKEDITOR.on( 'loaded', function()
			{
				editor.setData( html );
			});
	*/
}

function removeEditor()
{
   if ( !editor )
      return;

   // Retrieve the editor contents. In an Ajax application, this data would be
   // sent to the server or used in any other way.
   document.getElementById( 'editorcontents' ).innerHTML = editor.getData();
   document.getElementById( 'contents' ).style.display = '';

   // Destroy the editor.
   editor.destroy();
   editor = null;
}

function idg_calendar_change(id,d)
{
   var obj = $(id);
   var t = obj.val();

   // ParseFloat pour IE qui retourne 0 pour ParseInt("08")...
   var j = parseFloat(t.substr(0,2)) + parseInt(d);
   var m = parseFloat(t.substr(3,2));
   var a = parseInt(t.substr(6,4));
   var nd = new Date(a,m-1,j);
   j = nd.getDate();
   m = nd.getMonth()+1;
   a = new String(nd.getFullYear());
   
   var jj = new String(j);
   //if(j<10)
   //   jj = "0"+jj;
   var mm = new String(m);
   //if(m<10)
   //   mm = "0"+mm;
/*   
console.log("date="+t);    
console.log("jour="+j);    
console.log("mois="+m);    
console.log("annee="+a);    
console.log("day="+jj);    
console.log("final="+jj+"/"+m+"/"+a);
*/
   var pd = $.datepicker.parseDate('dd/mm/yy', jj+'/'+m+'/'+a);    
   obj.datepicker('setDate', pd);
   idg_datechange();
}

function idg_mobile_navigation(idact)
{
   document.forms[0].idact.value = idact;//$('#MENU_NAVIGATION').val();
   if(document.forms[0].idact.value!="")
      fs();
}

var idg_vkb_input = null;

function idg_vkb_init()
{
   if(idg_chk_opt("ui_vkb"))
      $("input[type='text'],input[type='textarea'],input[type='password']").not("[readonly]").bind("focus",function(){idg_vkb_show(this)}).bind("blur",function(){/*$("#idgvkb").hide();idg_vkb_input=null*/});
}

function idg_vkb_show(obj)
{
   var src = $(obj);
   var kbd = $("#idgvkb");
   if(!kbd.is(":visible") || idg_vkb_input != src)
   {
      idg_vkb_input = src;
      $.ajax({async:true,url:"../"+$("#idgver").val()+"/ics.php?idact=112"}).done(
         function(html)
         {
            src.css("z-index",1001);
            kbd.html(html)
            kbd.show();
            $("#idgvkbuni").show();
            idg_vkb_offset();
            $(".idgvkb b").bind("click",function(){
               idg_vkb_keyin(this)
               });
         });
   }
}

function idg_vkb_hide()
{
   $("#idgvkb").hide();
   idg_vkb_input=null; 
}

function idg_vkb_offset()
{
   var src = idg_vkb_input;
   var kbd = $("#idgvkb");
   var o = src.offset();
   var t = $("#idgvkbuni b:first");
   o.top += src.height() + 8;
   o.left -= (kbd.width()/2)-(src.width()/2);
   if(o.left<0)
      o.left = 0;
   kbd.offset(o);
   $('html, body').animate({
      scrollTop: src.offset().top - ($(window).height() - kbd.innerHeight())
      }, 500); 
}

function idg_vkb_keyin(obj)
{
   var src = $(obj);
   c = src.text();
   var w = null;
   switch(c)
   {
      case "Supprimer":
         var t = idg_vkb_input.val();
         if(t!="")
            idg_vkb_input.val(t.substring(0,t.length-1));
         break;
      case "Majuscules":
         w = $("#idgvkb .maj");
      case "Signes":
         if(w===null)
            w = $("#idgvkb .div");
         if(w.is(":visible"))
            w.hide();
         else
            w.show();
         idg_vkb_offset();
         break;
      case "Fermer":
      case "Valider":
         idg_vkb_hide();
         break;
      default:
         idg_vkb_input.val(idg_vkb_input.val()+c);
   }
//   if(idg_vkb_input!=null)
//      idg_vkb_input.focus();
}

var idg_cphelp_timeout = null;
var idg_cphelp_refresh = null;
var idg_cphelp_ask_timeout = null;
var idg_cphelp_count;
var idg_cphelp_contener;
var idg_cphelp_cp;
var idg_cphelp_loca;
var idg_cphelp_val;
var idg_iroot;
function idg_cp_hlp(contener,obj,cp,loca)
{  
   clearTimeout(idg_cphelp_ask_timeout);
   idg_cphelp_contener = contener;
   idg_cphelp_cp = cp;
   idg_cphelp_loca = loca;
   idg_cp_timeout(0);
   idg_cphelp_val = $(obj).val();
   if(idg_cphelp_val.length>2)
   {
      idg_cphelp_ask_timeout = setTimeout("idg_cp_ask()",500);
      //idg_cp_ask();
   }
}
function idg_cp_ask()
{
   $(idg_cphelp_contener).load("../"+$("#idgver").val()+"/ics.php?idact=114"/*IDACT_CP*/,{IDOBJ:idg_cphelp_val.concat('|').concat(idg_cphelp_cp).concat('|').concat(idg_cphelp_loca).concat('|').concat(idg_cphelp_contener)},function(){idg_cp_loaded()});
   idg_cp_timeout(5000);
}
function idg_cp_loaded()
{
   idg_cphelp_count=0;
   var obj = $(idg_cphelp_contener);
   if(obj.find('option').length==1)
   {
      obj.find("option:first").attr("selected","selected");
      idg_cp_sel(0);
      obj.hide();
   }
   else
   {
      obj.show().find("select").show();
   }
}
function idg_cp_timeout(d)
{
   if(d>0)
   {
      idg_cphelp_timeout = setTimeout("$('".concat(idg_cphelp_contener).concat("').find('select').fadeOut({duration:5000,easying:'linear'})"),d);
   }
   else
   {
      clearTimeout(idg_cphelp_timeout);
      $(idg_cphelp_contener).stop(true,true).show();//.height(16);
   }
}
function idg_cp_click(obj)
{
   if(idg_cphelp_count>0)
      idg_cp_switch(0);
   idg_cphelp_count++;
}
function idg_cp_sel(d)
{
   //$(idg_cphelp_loca).removeAttr("readonly");
   //if($("ID_PAYS").find("option:selected").val()=="FRA")
   idg_cp_timeout(d);
      var v = $(idg_cphelp_contener).find("option:selected").val();
      var n = v.split("|");
      if(n[0]!="" && n[1]!="")
      {
         $(idg_cphelp_cp).val(n[0]);
         $(idg_cphelp_loca).val(n[1]);
      }
   }
function idg_cp_switch(d)
{
   if(d>0)
   {
      idg_cphelp_refresh = setInterval("idg_cp_sel(0)",500);
   }
   else
	{
      clearTimeout(idg_cphelp_refresh);
      idg_cp_timeout(1);
   }
}

function idg_ecv(obj,ida)
{
	var t=($(obj).position().top)*0.05-250;
	if(t<10) t=10;
	$('#modal_ecv').text(g_idg_wait).css('top',String(t).concat('px'));
	idg_jqmShow('#modal_ecv');
	$('#modal_ecv').load("ics.php",{idact:'388',ida:ida,idses:$('input[name="idses"]').val()},function(){$('#bloc_etatcivil').show();});
}
var g_idg_submit = true;
var g_idg_rmsg = "Merci d'utiliser les boutons de l'application, les boutons 'Page precedente' et 'Page suivante' du navigateur ne sont pas supportés. Sélectionnez 'Annuler' pour continuer.\r\n";
var g_idg_wait="Veuillez patienter, chargement en cours...";
function idg_beforeunload()
{
   if(!g_idg_submit)
   {
      return g_idg_rmsg;
   }
}
function idg_unload()
{
}
$(document).ready(
   function()
   {
      $("#waitpage, .bloc_wait").hide();
      $("noscript").remove();
      if(idg_chk_opt("smartphone"))
      {
         g_idg_submit = false;
         var obj = $(".maintenance");
         if(obj.text()=="")
            obj.hide();
      }
      else
      {
         idg_ui_layer($("body"));
         idg_vkb_init();
         setTimeout("$('.erreur').fadeOut(2000)",30000);
      }
   }
   );
