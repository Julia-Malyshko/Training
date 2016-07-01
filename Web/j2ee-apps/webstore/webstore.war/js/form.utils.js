define(["jquery", "jquery-form"], function ($) {
	
	        function submitAjaxForm(formId) {
	        	var options = {
	        			success : function(data){
			        				if(data.status == "success") {
//			        					if(data.redirectUrl != None){
			        					//logout stay
			        					window.location = data.redirectUrl;				        						
			        					    					
		        					}else if(data.status == "error" && data.errors.length) {
		        						errorMessage = formErrorMessage(data.errors);
		        						openErrorMessage(errorMessage);
		        						scrollToTop();
		        					}
	        					},
	        			error : function(){
	        							openErrorMessage("<p><b>Your request cannot be processed. Please refresh " +
	        									"the page.</b></p>");
	        			},
	        			dataType : 'json',
	        			cache : false
	        	};
	        	var form = $("#" + formId);
	        	form.ajaxForm(options);
	        	form.ajaxSubmit(options);
	        	return false;
	        }
	        
	        
	        function formErrorMessage(errors){
	        	var errorMessage = "";
				for(var i = 0; i < errors.length; i++) {
					errorMessage +='<p>'+ errors[i]+'</p>';
				}
				return errorMessage;
	        }
	    
	        
	        function openErrorMessage(message) {
	        	   openMessageTarget(message, getErrorDiv());
	        	}

	        
        	function openMessageTarget(message, target) {
        	   var targetEl = $("#" + target);
        	   targetEl.html(message);

        	}

        	
        	function getErrorDiv() {
        	   var errorDiv = "error";
        	   return errorDiv;
        	}
        	
        	
        	function scrollToTop(){
        		$(window).scrollTop(0);
        	}
        	
        	
        	function clearErrors(){
        		$("#error").remove();
        	}


	        return {
	            submitAjaxForm : submitAjaxForm
	        };	        
	    });