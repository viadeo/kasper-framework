var graph_concept = function(g, node) {
	var concept = node_concept(node);

	var id_root = 'graphnode_'+node.name;
	g.addNode(id_root);//, concept);

/*
	var i = 1;
	var connectRelations = function(n, rel, inversion) {
		var target_rel_id = 'target_rel_'+node.name+'_'+rel.name+i++;
		var other_id = target_rel_id+'_ep';

		var a, b;
		if (inversion) {
			a = id_root;
			b = other_id;
		} else {
			a = other_id;
			b = id_root;
		}

		g.addNode(target_rel_id, node_relation(rel));
		g.addNode(other_id, node_concept({ name: rel.name, label: rel.name }));

		g.addEdge(a, target_rel_id, { directed: true });
		g.addEdge(target_rel_id, b, { directed: true });		
	};

	$.each(node.targetRelations, function(n, rel) {
		rel.name = rel.sourceConceptName;
		connectRelations(n, rel, true);
	});

	$.each(node.sourceRelations, function(n, rel) {
		rel.name = rel.targetConceptName;
		connectRelations(n, rel, false);
	});
*/

	g.addNode('a', node_relation());
	g.addNode('b', node_concept());

	g.addEdge(id_root, 'a', { directed: true });
	g.addEdge('a', 'b', { directed: true });	

};