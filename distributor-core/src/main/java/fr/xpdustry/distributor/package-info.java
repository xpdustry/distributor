@DefaultQualifier(
  value = NonNull.class,
  locations = {
    TypeUseLocation.EXPLICIT_LOWER_BOUND,
    TypeUseLocation.EXPLICIT_UPPER_BOUND,
    TypeUseLocation.IMPLICIT_LOWER_BOUND,
    TypeUseLocation.IMPLICIT_UPPER_BOUND,
    TypeUseLocation.LOWER_BOUND,
    TypeUseLocation.UPPER_BOUND,
    TypeUseLocation.CONSTRUCTOR_RESULT,
    TypeUseLocation.EXCEPTION_PARAMETER,
    TypeUseLocation.FIELD,
    TypeUseLocation.PARAMETER,
    TypeUseLocation.RECEIVER,
    TypeUseLocation.RESOURCE_VARIABLE,
    TypeUseLocation.RETURN,
    TypeUseLocation.OTHERWISE,
  }
)
package fr.xpdustry.distributor;

import org.checkerframework.checker.nullness.qual.*;
import org.checkerframework.framework.qual.*;