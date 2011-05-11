package chameleon.aspects.weaver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import chameleon.aspects.Aspect;
import chameleon.aspects.WeavingEncapsulator;
import chameleon.aspects.advice.Advice;
import chameleon.aspects.pointcut.expression.MatchResult;
import chameleon.core.compilationunit.CompilationUnit;
import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;

public abstract class WeaveTransformer {
	/**
	 * 	The object that performs the actual weaving. A chain of responsibility pattern is used for this object
	 */
	private Weaver elementWeaver;
	
	/**
	 * 	Constructor
	 */
	public WeaveTransformer() {
		initialiseWeavers();
	}
	
	protected abstract void initialiseWeavers();

	/**
	 * 	Weave the given compilation unit
	 * 
	 * 	@param 	compilationUnit
	 * 			The compilation unit to weave
	 * 	@param 	aspectCompilationUnits
	 * 			All compilation units that contain aspects
	 * 	@param 	otherCompilationUnits
	 * 			All other compilation units
	 * 	@return	The modified (woven) compilation unit
	 * 	@throws LookupException
	 */
	public CompilationUnit weave(CompilationUnit compilationUnit, List<CompilationUnit> aspectCompilationUnits, List<CompilationUnit> otherCompilationUnits) throws LookupException {
		if (!compilationUnit.hasDescendant(Aspect.class)) {
			Map<Element, List<WeavingEncapsulator>> weavingMap = weaveRegularType(compilationUnit, aspectCompilationUnits, otherCompilationUnits);
			
			for (Entry<Element, List<WeavingEncapsulator>> entry : weavingMap.entrySet()) {
				List<WeavingEncapsulator> weavingEncapsulators = entry.getValue();
				
				// Sort all weaving that has to be done
				Collections.sort(weavingEncapsulators, new AdviceTypeComparator());
				
				// Transform the weaving encapsulation list to a double linked list
				WeavingEncapsulator weavingChain = WeavingEncapsulator.fromIterable(weavingEncapsulators);
				
				// Start the weaving
				weavingChain.start();
			}
		}
		
		return compilationUnit;
	}
	
	/**
	 * 
	 * 	Weave a given regular type
	 * 
	 * 	@param 	compilationUnit
	 * 			The compilation unit to weave
	 * 	@param 	aspectCompilationUnits
	 * 			All compilation units that contain aspects
	 * 	@param 	otherCompilationUnits
	 * 			All other compilation units
	 * 	@return	The map of joinpoints to weaving encapsulators that handle this joinpoint
	 * 	@throws LookupException
	 */
	private Map<Element, List<WeavingEncapsulator>> weaveRegularType(CompilationUnit compilationUnit, List<CompilationUnit> aspectCompilationUnits, List<CompilationUnit> otherCompilationUnits) throws LookupException {
		// Get a list of all advices
		List<Advice> advices = new ArrayList<Advice>();
		for (CompilationUnit cu : aspectCompilationUnits) {
			advices.addAll(cu.descendants(Advice.class));
		}
		
		// Keep a map, per joinpoint: the weaving encapsulators that weave it
		Map<Element, List<WeavingEncapsulator>> weavingMap = new HashMap<Element, List<WeavingEncapsulator>>();
		
		// Weave all advices
		for (Advice<?> advice : advices) {
			// Get all joinpoints matched by that expression
			List<MatchResult<? extends Element>> joinpoints = advice.getExpandedPointcutExpression().joinpoints(compilationUnit);
			
			// For each joinpoint, get all necessairy weaving info and add it to the list
			for (MatchResult<? extends Element> joinpoint : joinpoints) {
				if (!weavingMap.containsKey(joinpoint.getJoinpoint()))
					weavingMap.put(joinpoint.getJoinpoint(), new ArrayList<WeavingEncapsulator>());
				
				WeavingEncapsulator encapsulator = getElementWeaver().start(advice, joinpoint);
				weavingMap.get(joinpoint.getJoinpoint()).add(encapsulator);
			}
		}
		
		return weavingMap;
	}	
	
	private Weaver getElementWeaver() {
		return elementWeaver;
	}

	protected void setElementWeaver(Weaver elementWeaver) {
		this.elementWeaver = elementWeaver;
	}
}
