// Init script

"use strict";

// these are not strictly necessary, but are kept for edge cases
const run = method => new Runnable(){run: method}
const boolf = method => new Boolf(){get: method}
const boolp = method => new Boolp(){get: method}
const floatf = method => new Floatf(){get: method}
const floatp = method => new Floatp(){get: method}
const cons = method => new Cons(){get: method}
const prov = method => new Prov(){get: method}
const func = method => new Func(){get: method}
const runner = method => new Packages.arc.util.CommandHandler.CommandRunner(){accept: method}

const debug = function(/*text, args...*/){Log.debug(arguments[0], Array.from(arguments).splice(1, arguments.length))}
const info = function(/*text, args...*/){Log.info(arguments[0], Array.from(arguments).splice(1, arguments.length))}
const warn = function(/*text, args...*/){Log.warn(arguments[0], Array.from(arguments).splice(1, arguments.length))}
const err = function(/*text, args...*/){Log.err(arguments[0], Array.from(arguments).splice(1, arguments.length))}

const print = text => Packages.java.lang.System.out.print(text)
const println = text => Packages.java.lang.System.out.println(text)
const printf = function(/*text, args...*/){Packages.java.lang.System.out.printf(arguments[0], Array.from(arguments).splice(1, arguments.length))}

const jCall = Call
Call = Packages.mindustry.gen.Call

//js 'extend(Base, ..., {})' = java 'new Base(...) {}'
const extend = function (/*Base, ..., def*/) {
  let Base = arguments[0]
  let def = arguments[arguments.length - 1]
  //swap order from Base, def, ... to Base, ..., def
  let args = [Base, def].concat(Array.from(arguments).splice(1, arguments.length - 2))

  //forward constructor arguments to new JavaAdapter
  let instance = JavaAdapter.apply(null, args)
  //JavaAdapter only overrides functions; set fields too
  for (let i in def) {
    if (typeof (def[i]) != "function") {
      instance[i] = def[i]
    }
  }

  return instance
}
