
jQuery.fn.smartSort = (function(){
 
    var sort = [].sort;
 
    return function(comparator, columnToSort) {//this is the argument of the function
    	
       //Get all the element that to be sorted
       var elmToSort = columnToSort.find('td').filter(function(){return $(this).index()===columnToSort.index();});
 
        var placements = this.map(function(){
 
            var sortElement = getSortable.call(this),
                parentNode = sortElement.parentNode,
 
                // Since the element itself will change position, we have
                // to have some way of storing its original position in
                // the DOM. The easiest way is to have a 'flag' node:
                nextSibling = parentNode.insertBefore(
                    document.createTextNode(''),
                    sortElement.nextSibling
                );
 
            return function() {
 
                if (parentNode === this) {
                    throw new Error(
                        "You can't sort elements if any one is a descendant of another."
                    );
                }
 
                // Insert before flag:
                parentNode.insertBefore(this, nextSibling);
                // Remove flag:
                parentNode.removeChild(nextSibling);
 
            };
 
        });
 
        return sort.call(this, comparator).each(function(i){
            placements[i].call(getSortable.call(this));
        });
 
    };
 
})();


function genericComparator(a,b){
	inverse = false;
	a = $(a).text();
    b = $(b).text();
    
    return (
        isNaN(a) || isNaN(b) ?
            a > b : +a > +b
        ) ?
            inverse ? -1 : 1 :
            inverse ? 1 : -1;
}
