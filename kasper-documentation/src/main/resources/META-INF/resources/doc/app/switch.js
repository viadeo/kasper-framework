
var cache_layers = {};
var cache_tpls = {};

/**
 * Get a well filled node, if any and if necessary
 */
var getNode = function(node, callback) {
	if (!node || (node.type == 'domain')) {
		callback(node);
	} else {
		$.ajax({
            // FIXME: static var !
	    	url: "../kasper/doc" + node.url
	    }).done(function ( data ) {

	    	if (data.aggregate || data.aggregate === false) {
	        	data.entity = true;
	        }

	    	callback(data);

	    }).fail(function() {
	    	callback(node);
	    });
	}
}

/**
 * Builds a Kasper doc node layer component (as deferred)
 */
var buildNodeComponent = function(type, node, composeName) {
	return $.Deferred(function( deferred_obj ){

		var tpl_id = type + (composeName ? '-' + node.type : '');

		var do_tpl = function(tpl, node) {
			if (tpl) {
				var component = tpl({ node: node });
				deferred_obj.resolve( component );
			} else {
				deferred_obj.resolve();
			}
		}

		if (cache_tpls[tpl_id]) {
			do_tpl(cache_tpls[tpl_id], node);
		} else {
			$.get("tpl/" + tpl_id + ".hbars").done( function(source) {
				var template = Handlebars.compile(source);
				cache_tpls[tpl_id] = template;
		  		do_tpl(cache_tpls[tpl_id], node);
			}).fail(function() {
				do_tpl();
			});
		}

	}).promise();
}

/**
 * Build the graph for this node
 */
var buildNodeGraph = function(div_id, node) {
	return $.Deferred(function( deferred_obj ) {

		if (node.type == "domain") {
			deferred_obj.resolve();
		}

		$.getScript("app/graph.js").done(function() {
			getGraphForNode(div_id, node, deferred_obj.resolve);
		}).fail(function() { deferred_obj.resolve(); });

	}).promise();
}

/**
 * Builds a Kasper doc node layer
 */
var buildNodeContents = function(div_id, node, callback) {	
	var layer_id = 'layer_' + node.name;

	$('#'+div_id).append("<div id='" + layer_id + "' class=''></div>");
	$('#'+layer_id).hide();
	cache_layers[node.name] = layer_id;

	$.when( 
			
			buildNodeComponent('header', node, true), 
			buildNodeComponent('header', node)
			//,buildNodeGraph(layer_id, node)

		).then(function(nheader, header) {

			if (!header) {
				callback(null);
			}

			if (nheader) {
				$('#'+layer_id).prepend(nheader);
			}

			$('#'+layer_id).prepend(header);

			callback(layer_id);

	}, function() {
		console.log("ERROR when loading layer for node " + node.name);
		callback(null);
	});
}

/**
 * Given a Kasper doc node and the parent contents div, build it (if necessary) and switch contents
 */
var switchNodeContents = function(div_id, node) {
	if (node && node.type && node.name) {
		
		var switch_layer = function(layer_id) {
			if (layer_id) {
				$("#"+div_id+" > div").hide();
				$("#"+layer_id).fadeIn("slow");
			}
		}

		if (cache_layers[node.name]) {
			layer_id = cache_layers[node.name];
			switch_layer(layer_id);
		} else {
			getNode(node, function(node) {
				buildNodeContents(div_id, node, switch_layer);
			});
		}

	}
}
