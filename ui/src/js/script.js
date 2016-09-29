var CostPricing = CostPricing || {};

CostPricing.module = (function () {
    var fn, api;

    fn = {
        _title: function(object) {
            console.log(object);
            $('.upper-data').remove();
            $tableRow = jQuery('<div />')   .attr('class', 'metric-sections upper-data col-xs-12');
            $child = jQuery('<div />') .attr('class', 'col-xs-4');
            $child1 = jQuery('<div />') .attr('class', 'col-xs-8');

            $innerofchild = jQuery('<div />').attr({'class': 'border-radius', 'style': 'background-color: ' + object.color}).css('background-color', object.color);
            $innerofchild11 = jQuery('<h5 />').attr('class', 'col-xs-12').text(object.name);
            $innerofchild12 = jQuery('<p />').attr('class', 'col-xs-12').html('TOTAL COST: ' + '<span id="all-cost" class="cost"> $' + object.size + '</span>');

            $child.append($innerofchild);

            $child1.append($innerofchild11);
            $child1.append($innerofchild12);

            $tableRow.append($child);
            $tableRow.append($child1);
            jQuery('#upperData')   .append($tableRow);
        },
        _populateTable: function(obj) {
        console.log(obj);
        jQuery('#tableData').html('');
        obj.forEach(function (object) {
        $tableRow = jQuery('<div />')   .attr('class', 'table-row ');
                    $child = jQuery('<span />') .attr('class', 'table-key') .text(object.name);
                    $child1 = jQuery('<span />') .attr('class', 'table-value').text('$' + object.size);

                    $tableRow.append($child);
                    $tableRow.append($child1);
                    jQuery('#tableData')   .append($tableRow);
        });

        }
    };

    api = {
        populateTable: function () {
            return fn._populateTable.apply(this, arguments);
        },
        title: function () {
            return fn._title.apply(this, arguments);
        },
        validate: function () {
            var uname = jQuery('#uname').val();
            var pword = jQuery('#pword').val();

            if(uname === 'admin' && pword === 'admin') window.location.href = '/dashboard';
        }
    };

    return api;
})();