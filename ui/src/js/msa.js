//Note: Size of the chart can be adjusted here
var width = 500,
    height = 600,
    radius = Math.min(width, height) / 2;

var x = d3.scale.linear()
    .range([0, 2 * Math.PI]);

var y = d3.scale.linear()
    .range([0, radius]);

var color = d3.scale.category20c();

var nameToColorMapping = {}

var listOfColors = [d3.rgb("#ececec"),d3.rgb("#407574"),
d3.rgb("#407574"),d3.rgb("#9e7046"),
d3.rgb("#a6e6a3"),d3.rgb("#a0cfe2"),
d3.rgb("#8a96b8"),d3.rgb("#be7d83"),
d3.rgb("#94a372"),d3.rgb("#4facc4"),
d3.rgb("#f1bd7e"),d3.rgb("#ececec")]

var counter = 0;

//Note: Colours for each section can be changed here
function colorFor(name) {

   var mappedColor = nameToColorMapping[name];

   if(mappedColor == null || mappedColor == 'undefined'){
        mappedColor = listOfColors[counter];
        nameToColorMapping[name] = mappedColor;
        counter++;
   }
//
//
//  var nodeColor = d3.rgb("#000000");
//  switch (name) {
//    case "All":
//      nodeColor = d3.rgb("#ececec");
//      break;
//    case "Over All Cost":
//      nodeColor = d3.rgb("#ececec");
//      break;
//    case "Order":
//      nodeColor = d3.rgb("#407574");
//      break;
//    case "Demands Management":
//      nodeColor = d3.rgb("#407574");
//      break;
//    case "Inventory":
//      nodeColor = d3.rgb("#9e7046");
//      break;
//    case "Supply Management":
//      nodeColor = d3.rgb("#9e7046");
//      break;
//    case "Catalog Service":
//      nodeColor = d3.rgb("#94a372");
//      break;
//    case "Customer":
//      nodeColor = d3.rgb("#4facc4");
//      break;
//    case "Order Service":
//      nodeColor = d3.rgb("#8a96b8");
//      break;
//    case "Load Balancer":
//      nodeColor = d3.rgb("#f1bd7e");
//      break;
//    case "Discovery":
//      nodeColor = d3.rgb("#a6e6a3");
//      break;
//    case "Metrics":
//      nodeColor = d3.rgb("#a0cfe2");
//      break;
//    case "Supply Chain":
//      nodeColor = d3.rgb("#be7d83");
//      break;
//
//    default:
//  }
return mappedColor;
}

//Note: We attach the svg to the chart element in html
var svg = d3.select("#chart").append("svg")
    .attr("width", width)
    .attr("height", height)
    .append("g")
    .attr("transform", "translate(" + width / 2 + "," + (height / 2 + 10) + ")");

var partition = d3.layout.partition()
    .value(function(d) { return d.size; });

var arc = d3.svg.arc()
    .startAngle(function(d) { return Math.max(0, Math.min(2 * Math.PI, x(d.x))); })
    .endAngle(function(d) { return Math.max(0, Math.min(2 * Math.PI, x(d.x + d.dx))); })
    .innerRadius(function(d) { return Math.max(0, y(d.y)); })
    .outerRadius(function(d) { return Math.max(0, y(d.y + d.dy)); });


var chartHandler = function(error, root) {

    if (error) return console.warn(error);


  var g = svg.selectAll("g")
      .data(partition.nodes(root))
    .enter().append("g");

  var path = g.append("path")
    .attr("d", arc)
    .style("fill", function(d) { return colorFor(d.name); })
    .on("click", click);

  var text = g.append("text")
    .attr("transform", function(d) { return "rotate(" + computeTextRotation(d) + ")"; })
    .attr("x", function(d) { return y(d.y); })
    .attr("dx", function(d) { if(d.name == "All") return -10; else return 8; }) // margin
    .attr("dy", ".35em") // vertical-align
    .text(function(d) { return d.name; });


//Note: This adds a row to the details section.
//We delete the old table and add an empty one first, to which we add the new entries
function addRow(name, value, colour) {
    if (!document.getElementsByTagName) return;

    old_tbody = document.getElementsByTagName("tbody").item(0);

    var new_tbody = document.createElement('tbody');
    old_tbody.parentNode.replaceChild(new_tbody, old_tbody)

    //Make an SVG Container
    svgContainer = d3.select("tbody").append("svg")
                                             .attr("width", 60)
                                            .attr("height", 60);

    //Draw the Circle of the colour of the selected section
    circle = svgContainer.append("circle")
                               .attr("cx", 30)
                               .attr("cy", 30)
                               .attr("r", 25)
                               .attr("fill", colorFor(name));

    //Create row
    row = document.createElement("tr");

    //Create each cell of the row
    cell1 = document.createElement("td");
    cell2 = document.createElement("td");
    textnode1=document.createTextNode(name);
    textnode2=document.createTextNode(value);

    cell1.appendChild(textnode1);
    cell2.appendChild(textnode2);
    row.appendChild(cell1);
    row.appendChild(cell2);
    new_tbody.appendChild(row);

    //Note: Add new rows like above if more entries are needed for the table

}

function getAncestors(node) {
  var path = [];
  var current = node;
  while (current.parent) {
    path.unshift(current);
    current = current.parent;
  }
  return path;
}

//Note: Get a list of all children
function getDescendants(node) {
  var descendants = new Array();
  descendants.push(node);

  if(node.children) {
    node.children.forEach(function(d) {
      descendants = descendants.concat(getDescendants(d));
    })
  }

  return descendants;
}

//Note: Highlight all the children of this node
function isDescendantOf(child, parent) {
    if(!parent.children) {
        return false;
    }

    parent.children.forEach(function(d) {
      if(d == child.parent)
        return true;
      else
        return isDescendantOf(d);
    })

    return false;
}

//Note: This does all the processing when a section is clicked
function click(d) {
    //If root node (All) then reset the visuals
    if(d.name == "All")
    {
      text.transition().attr("opacity", 1);
      d3.selectAll("path")
          .transition().attr("opacity", 1);
    }
    else {
      // fade out all text elements
      text.transition().attr("opacity", 0.3);

      //fade out all other elements apart from the selected one
      //path.transition().attr("opacity", 0);
      d3.selectAll("path")
          .transition().attr("opacity", 0.3);

      var childArray = getDescendants(d);

      // Then highlight only those that have the same name as the selected one/ or who are the children of this one
      g.selectAll("path")
        .filter(function(node) {
                  return (node.name == d.name || (childArray.indexOf(node) >= 0));
                })
        .transition().attr("opacity", 1);


      g.selectAll("text")
            .filter(function(node) {
                      return (node.name == d.name || (childArray.indexOf(node) >= 0));
                    })
            .transition().attr("opacity", 1);

    }

    //Get the addition of all sizes
    var total = 0;
    g.selectAll("path")
       .filter(function(node) {
                 if (node.name == d.name)
                  total += node.size;
              });

    //Note: This adds entries to the details tables on the right
    addRow(d.name, total, d.color);
  }
};


//Note: We are reading the json here
//d3.json("msa.json", function(error, root) {

//d3.json("msa.json", chartHandler);
d3.json("/msvc/business/function/costing/consolidated", chartHandler);
d3.select(self.frameElement).style("height", height + "px");
//$.getJSON( "/msvc/business/function/costing/consolidated", function( data ) {
//  console.log(data);
//  d3.json(data, chartHandler);
//  d3.select(self.frameElement).style("height", height + "px");
//});



// Interpolate the scales!
function arcTween(d) {
  var xd = d3.interpolate(x.domain(), [d.x, d.x + d.dx]),
      yd = d3.interpolate(y.domain(), [d.y, 1]),
      yr = d3.interpolate(y.range(), [d.y ? 20 : 0, radius]);
  return function(d, i) {
    return i
        ? function(t) { return arc(d); }
        : function(t) { x.domain(xd(t)); y.domain(yd(t)).range(yr(t)); return arc(d); };
  };
}

function computeTextRotation(d) {
  if(d.name == "All")
    return 0;
  else
    return (x(d.x + d.dx / 2) - Math.PI / 2) / Math.PI * 180;
}