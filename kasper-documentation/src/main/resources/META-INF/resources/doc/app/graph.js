
var getGraphForNode = function(div_id, node, callback) {
	var graph_id = 'graph_' + node.name;
	var graph = $('<div/>', { id: graph_id, class: 'center' });
	$("#"+div_id).append(graph);

	var g = new Graph();

	$.getScript("app/graph_elements.js").done(function() {
		$.getScript("app/graph_"+node.type+".js").done(function() {

			window["graph_"+node.type](g, node);

			/* layout the graph using the Spring layout implementation */
		    var layouter = new Graph.Layout.Spring(g);
		    layouter.layout();

		    /* draw the graph using the RaphaelJS draw implementation */
		    var renderer = new Graph.Renderer.Raphael(graph_id, g, 800, 400);
		    renderer.draw();
		    
		    redraw = function() {
		        layouter.layout();
		        renderer.draw();
		    };

		    callback(graph);

		}).fail(function() {
			console.log("Error loading graph definition for node type : " + node.type);
			callback(); 
		});

	}).fail(function() { 
		console.log("Error loading graph elements");
		callback(); 
	});
}