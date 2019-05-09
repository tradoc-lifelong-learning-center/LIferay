(function(){
	smoothScrollPolyfill();
	window.onload = scrollTables;



	function scrollTables() {
		  /*sets how far table scrolls onclick*/
		  var scrollDistance = 100;

		  
		  //find all tables with data-srcrollable="true" tag and add wrappers, buttons
		  var tables = document.querySelectorAll("*[data-scrollable-table='true']");
		  
		  wrapTables(tables);
		  
		  var tableContainers = document.getElementsByClassName("table-container");
		  
		  var buttons = document.getElementsByClassName("scroll-button");
		  
		  //determine initial state of table scroll bars
		  showHideScrollButtons();

		  /*bind click handler to button*/
		  for (var i = 0; i < buttons.length; i++) {
			  
		    buttons[i].onclick = function(e) {
		      var direction = e.currentTarget.getAttribute("data-direction");
		      var linkedTableID = e.currentTarget.getAttribute("data-linked-table");
		      var table = document.getElementById(linkedTableID);

		      //table.scrollBy(scrollDistance * direction, 0);

		      table.scrollBy({
		        top: 0,
		        left: scrollDistance * direction,
		        behavior: "smooth"
		      });
		    };
		  }

		  /*bind scroll handler to table containers*/
		  for (var i = 0; i < tableContainers.length; i++) {
		    tableContainers[i].addEventListener("scroll", showHideScrollButtons);
		  }

		  /*On resize, remove scroll bars*/
		  //throttle resize based on MDN: https://developer.mozilla.org/en-US/docs/Web/Events/resize
		  window.addEventListener("resize", resizeThrottler, false);
		  var resizeTimeout;

		  function actualResizeHandler() {
		    showHideScrollButtons();
		  }

		  /*should this be broken up into multiple fns?*/
		  function showHideScrollButtons() {
		    /*loop through all tables and get button elements*/
		    for (var i = 0; i < tableContainers.length; i++) {
		      var tbContainer = tableContainers[i];
		      var id = tbContainer.getAttribute("id");
		      
		      var buttons = document.querySelectorAll(
		        '[data-linked-table="' + id + '"]'
		      );

		      //Then loop through buttons and determine visibility
		      for (var b = 0; b < buttons.length; b++) {
		        let button = buttons[b];
		        let direction = button.getAttribute("data-direction"); //in HTML, 1 = right, -1 = left
		        let scrollWidth = tbContainer.scrollWidth;
		        let offsetWidth = tbContainer.offsetWidth;
		        let scrollLeft = tbContainer.scrollLeft;
		        
		        //If scrollBy isn't supported (IE), hide buttons
		        try{
		          tbContainer.scrollBy(0,0);
		          //console.log("can scroll by! ");
		        }
		        catch(error){
		          hideElement(button);
		          //console.log("can't scroll by... ");
		          return false;
		        }


		        //for left button
		        if (direction == -1) {
		          if (scrollLeft == 0) {
		            hideElement(button);
		          } else {
		            showElementInline(button);
		          }
		        }

		        //for right button
		        if (direction == 1) {
		          if (offsetWidth + Math.ceil(scrollLeft) >= scrollWidth || offsetWidth == scrollWidth) {
		            hideElement(button);
		          } else {
		            showElementInline(button);
		          }
		        }
		      }
		    }
		  }

		  function showElementInline(e) {
		    e.style.display = "inline";
		  }

		  function hideElement(e) {
		    e.style.display = "none";
		  }

		  function resizeThrottler() {
		    // ignore resize events as long as an actualResizeHandler execution is in the queue
		    if (!resizeTimeout) {
		      resizeTimeout = setTimeout(function() {
		        resizeTimeout = null;
		        actualResizeHandler();

		        // The actualResizeHandler will execute at a rate of 15fps
		      }, 66);
		    }
		  }
		  
		  function wrapTables(tableContainers){

				for(var i = 0; i<tableContainers.length; i++){
					var tableContainerId = "table-container-" + i;
					
					var outerContainer = '<div class="table-scroller table-group-container">';
					var innerContainer = '<div class="table-container" id="' + tableContainerId + '">';
					var leftButton = '<button class="scroll-button scroll-button--left" data-direction="-1" data-linked-table="' + tableContainerId + '"><svg height="22.6" id="Layer_1" width="12.9" xmlns="http://www.w3.org/2000/svg"><style>.st0{fill:none;stroke:#fff;stroke-width:3;stroke-linecap:round;stroke-linejoin:round}</style><g id="Layer_2"><g id="_00B5E2"><path class="st0" d="M1.6 11.4l9.8 9.7M11.3 1.5l-9.8 9.8"/></g></g></svg></button>';
					var rightButton = '<button class="scroll-button scroll-button--right" data-direction="1" data-linked-table="' + tableContainerId + '"><svg height="22.6" id="Layer_1" width="12.9" xmlns="http://www.w3.org/2000/svg"><style>.st0{fill:none;stroke:#fff;stroke-width:3;stroke-linecap:round;stroke-linejoin:round}</style><g id="Layer_2"><g id="_00B5E2"><path class="st0" d="M11.3 11.4l-9.8 9.7M1.6 1.5l9.8 9.8"/></g></g></svg></button>';
					
					tableContainers[i].outerHTML = outerContainer + leftButton + innerContainer+ tableContainers[i].outerHTML + "</div>" + rightButton + "</div>";
					
				}
			}

			
			function getElementId(element,idIndex){
				//return element ID. If there isn't one, add it
				if(element.id=="" || !element.id){
					element.id = "table-" + idIndex;
					return element.id;
				} else{
					return element.id;
				}
			}
	};

	

	// polyfill
	function smoothScrollPolyfill() {
	//Copyright (c) 2013 Dustan Kasten
	//The MIT License (MIT)
	//https://github.com/iamdustan/smoothscroll
		
	  // aliases
	  var w = window;
	  var d = document;

	  // return if scroll behavior is supported and polyfill is not forced
	  if (
	    'scrollBehavior' in d.documentElement.style &&
	    w.__forceSmoothScrollPolyfill__ !== true
	  ) {
	    return;
	  }

	  // globals
	  var Element = w.HTMLElement || w.Element;
	  var SCROLL_TIME = 468;

	  // object gathering original scroll methods
	  var original = {
	    scroll: w.scroll || w.scrollTo,
	    scrollBy: w.scrollBy,
	    elementScroll: Element.prototype.scroll || scrollElement,
	    scrollIntoView: Element.prototype.scrollIntoView
	  };

	  // define timing method
	  var now =
	    w.performance && w.performance.now
	      ? w.performance.now.bind(w.performance)
	      : Date.now;

	  /**
	   * indicates if a the current browser is made by Microsoft
	   * @method isMicrosoftBrowser
	   * @param {String} userAgent
	   * @returns {Boolean}
	   */
	  function isMicrosoftBrowser(userAgent) {
	    var userAgentPatterns = ['MSIE ', 'Trident/', 'Edge/'];

	    return new RegExp(userAgentPatterns.join('|')).test(userAgent);
	  }

	  /*
	   * IE has rounding bug rounding down clientHeight and clientWidth and
	   * rounding up scrollHeight and scrollWidth causing false positives
	   * on hasScrollableSpace
	   */
	  var ROUNDING_TOLERANCE = isMicrosoftBrowser(w.navigator.userAgent) ? 1 : 0;

	  /**
	   * changes scroll position inside an element
	   * @method scrollElement
	   * @param {Number} x
	   * @param {Number} y
	   * @returns {undefined}
	   */
	  function scrollElement(x, y) {
	    this.scrollLeft = x;
	    this.scrollTop = y;
	  }

	  /**
	   * returns result of applying ease math function to a number
	   * @method ease
	   * @param {Number} k
	   * @returns {Number}
	   */
	  function ease(k) {
	    return 0.5 * (1 - Math.cos(Math.PI * k));
	  }

	  /**
	   * indicates if a smooth behavior should be applied
	   * @method shouldBailOut
	   * @param {Number|Object} firstArg
	   * @returns {Boolean}
	   */
	  function shouldBailOut(firstArg) {
	    if (
	      firstArg === null ||
	      typeof firstArg !== 'object' ||
	      firstArg.behavior === undefined ||
	      firstArg.behavior === 'auto' ||
	      firstArg.behavior === 'instant'
	    ) {
	      // first argument is not an object/null
	      // or behavior is auto, instant or undefined
	      return true;
	    }

	    if (typeof firstArg === 'object' && firstArg.behavior === 'smooth') {
	      // first argument is an object and behavior is smooth
	      return false;
	    }

	    // throw error when behavior is not supported
	    throw new TypeError(
	      'behavior member of ScrollOptions ' +
	        firstArg.behavior +
	        ' is not a valid value for enumeration ScrollBehavior.'
	    );
	  }

	  /**
	   * indicates if an element has scrollable space in the provided axis
	   * @method hasScrollableSpace
	   * @param {Node} el
	   * @param {String} axis
	   * @returns {Boolean}
	   */
	  function hasScrollableSpace(el, axis) {
	    if (axis === 'Y') {
	      return el.clientHeight + ROUNDING_TOLERANCE < el.scrollHeight;
	    }

	    if (axis === 'X') {
	      return el.clientWidth + ROUNDING_TOLERANCE < el.scrollWidth;
	    }
	  }

	  /**
	   * indicates if an element has a scrollable overflow property in the axis
	   * @method canOverflow
	   * @param {Node} el
	   * @param {String} axis
	   * @returns {Boolean}
	   */
	  function canOverflow(el, axis) {
	    var overflowValue = w.getComputedStyle(el, null)['overflow' + axis];

	    return overflowValue === 'auto' || overflowValue === 'scroll';
	  }

	  /**
	   * indicates if an element can be scrolled in either axis
	   * @method isScrollable
	   * @param {Node} el
	   * @param {String} axis
	   * @returns {Boolean}
	   */
	  function isScrollable(el) {
	    var isScrollableY = hasScrollableSpace(el, 'Y') && canOverflow(el, 'Y');
	    var isScrollableX = hasScrollableSpace(el, 'X') && canOverflow(el, 'X');

	    return isScrollableY || isScrollableX;
	  }

	  /**
	   * finds scrollable parent of an element
	   * @method findScrollableParent
	   * @param {Node} el
	   * @returns {Node} el
	   */
	  function findScrollableParent(el) {
	    while (el !== d.body && isScrollable(el) === false) {
	      el = el.parentNode || el.host;
	    }

	    return el;
	  }

	  /**
	   * self invoked function that, given a context, steps through scrolling
	   * @method step
	   * @param {Object} context
	   * @returns {undefined}
	   */
	  function step(context) {
	    var time = now();
	    var value;
	    var currentX;
	    var currentY;
	    var elapsed = (time - context.startTime) / SCROLL_TIME;

	    // avoid elapsed times higher than one
	    elapsed = elapsed > 1 ? 1 : elapsed;

	    // apply easing to elapsed time
	    value = ease(elapsed);

	    currentX = context.startX + (context.x - context.startX) * value;
	    currentY = context.startY + (context.y - context.startY) * value;

	    context.method.call(context.scrollable, currentX, currentY);

	    // scroll more if we have not reached our destination
	    if (currentX !== context.x || currentY !== context.y) {
	      w.requestAnimationFrame(step.bind(w, context));
	    }
	  }

	  /**
	   * scrolls window or element with a smooth behavior
	   * @method smoothScroll
	   * @param {Object|Node} el
	   * @param {Number} x
	   * @param {Number} y
	   * @returns {undefined}
	   */
	  function smoothScroll(el, x, y) {
	    var scrollable;
	    var startX;
	    var startY;
	    var method;
	    var startTime = now();

	    // define scroll context
	    if (el === d.body) {
	      scrollable = w;
	      startX = w.scrollX || w.pageXOffset;
	      startY = w.scrollY || w.pageYOffset;
	      method = original.scroll;
	    } else {
	      scrollable = el;
	      startX = el.scrollLeft;
	      startY = el.scrollTop;
	      method = scrollElement;
	    }

	    // scroll looping over a frame
	    step({
	      scrollable: scrollable,
	      method: method,
	      startTime: startTime,
	      startX: startX,
	      startY: startY,
	      x: x,
	      y: y
	    });
	  }

	  // ORIGINAL METHODS OVERRIDES
	  // w.scroll and w.scrollTo
	  w.scroll = w.scrollTo = function() {
	    // avoid action when no arguments are passed
	    if (arguments[0] === undefined) {
	      return;
	    }

	    // avoid smooth behavior if not required
	    if (shouldBailOut(arguments[0]) === true) {
	      original.scroll.call(
	        w,
	        arguments[0].left !== undefined
	          ? arguments[0].left
	          : typeof arguments[0] !== 'object'
	            ? arguments[0]
	            : w.scrollX || w.pageXOffset,
	        // use top prop, second argument if present or fallback to scrollY
	        arguments[0].top !== undefined
	          ? arguments[0].top
	          : arguments[1] !== undefined
	            ? arguments[1]
	            : w.scrollY || w.pageYOffset
	      );

	      return;
	    }

	    // LET THE SMOOTHNESS BEGIN!
	    smoothScroll.call(
	      w,
	      d.body,
	      arguments[0].left !== undefined
	        ? ~~arguments[0].left
	        : w.scrollX || w.pageXOffset,
	      arguments[0].top !== undefined
	        ? ~~arguments[0].top
	        : w.scrollY || w.pageYOffset
	    );
	  };

	  // w.scrollBy
	  w.scrollBy = function() {
	    // avoid action when no arguments are passed
	    if (arguments[0] === undefined) {
	      return;
	    }

	    // avoid smooth behavior if not required
	    if (shouldBailOut(arguments[0])) {
	      original.scrollBy.call(
	        w,
	        arguments[0].left !== undefined
	          ? arguments[0].left
	          : typeof arguments[0] !== 'object' ? arguments[0] : 0,
	        arguments[0].top !== undefined
	          ? arguments[0].top
	          : arguments[1] !== undefined ? arguments[1] : 0
	      );

	      return;
	    }

	    // LET THE SMOOTHNESS BEGIN!
	    smoothScroll.call(
	      w,
	      d.body,
	      ~~arguments[0].left + (w.scrollX || w.pageXOffset),
	      ~~arguments[0].top + (w.scrollY || w.pageYOffset)
	    );
	  };

	  // Element.prototype.scroll and Element.prototype.scrollTo
	  Element.prototype.scroll = Element.prototype.scrollTo = function() {
	    // avoid action when no arguments are passed
	    if (arguments[0] === undefined) {
	      return;
	    }

	    // avoid smooth behavior if not required
	    if (shouldBailOut(arguments[0]) === true) {
	      // if one number is passed, throw error to match Firefox implementation
	      if (typeof arguments[0] === 'number' && arguments[1] === undefined) {
	        throw new SyntaxError('Value could not be converted');
	      }

	      original.elementScroll.call(
	        this,
	        // use left prop, first number argument or fallback to scrollLeft
	        arguments[0].left !== undefined
	          ? ~~arguments[0].left
	          : typeof arguments[0] !== 'object' ? ~~arguments[0] : this.scrollLeft,
	        // use top prop, second argument or fallback to scrollTop
	        arguments[0].top !== undefined
	          ? ~~arguments[0].top
	          : arguments[1] !== undefined ? ~~arguments[1] : this.scrollTop
	      );

	      return;
	    }

	    var left = arguments[0].left;
	    var top = arguments[0].top;

	    // LET THE SMOOTHNESS BEGIN!
	    smoothScroll.call(
	      this,
	      this,
	      typeof left === 'undefined' ? this.scrollLeft : ~~left,
	      typeof top === 'undefined' ? this.scrollTop : ~~top
	    );
	  };

	  // Element.prototype.scrollBy
	  Element.prototype.scrollBy = function() {
	    // avoid action when no arguments are passed
	    if (arguments[0] === undefined) {
	      return;
	    }

	    // avoid smooth behavior if not required
	    if (shouldBailOut(arguments[0]) === true) {
	      original.elementScroll.call(
	        this,
	        arguments[0].left !== undefined
	          ? ~~arguments[0].left + this.scrollLeft
	          : ~~arguments[0] + this.scrollLeft,
	        arguments[0].top !== undefined
	          ? ~~arguments[0].top + this.scrollTop
	          : ~~arguments[1] + this.scrollTop
	      );

	      return;
	    }

	    this.scroll({
	      left: ~~arguments[0].left + this.scrollLeft,
	      top: ~~arguments[0].top + this.scrollTop,
	      behavior: arguments[0].behavior
	    });
	  };

	  // Element.prototype.scrollIntoView
	  Element.prototype.scrollIntoView = function() {
	    // avoid smooth behavior if not required
	    if (shouldBailOut(arguments[0]) === true) {
	      original.scrollIntoView.call(
	        this,
	        arguments[0] === undefined ? true : arguments[0]
	      );

	      return;
	    }

	    // LET THE SMOOTHNESS BEGIN!
	    var scrollableParent = findScrollableParent(this);
	    var parentRects = scrollableParent.getBoundingClientRect();
	    var clientRects = this.getBoundingClientRect();

	    if (scrollableParent !== d.body) {
	      // reveal element inside parent
	      smoothScroll.call(
	        this,
	        scrollableParent,
	        scrollableParent.scrollLeft + clientRects.left - parentRects.left,
	        scrollableParent.scrollTop + clientRects.top - parentRects.top
	      );

	      // reveal parent in viewport unless is fixed
	      if (w.getComputedStyle(scrollableParent).position !== 'fixed') {
	        w.scrollBy({
	          left: parentRects.left,
	          top: parentRects.top,
	          behavior: 'smooth'
	        });
	      }
	    } else {
	      // reveal element in viewport
	      w.scrollBy({
	        left: clientRects.left,
	        top: clientRects.top,
	        behavior: 'smooth'
	      });
	    }
	  };
	}	
	
	
	
	
	
})();
	