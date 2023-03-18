package yahoofinance.util

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j

@Slf4j
class MessageUtils {

    def static loadFile(fileName, replacementMap = [:]) {
        try {
            def string = MessageUtils.class.getResource(fileName).text

            def json = new JsonSlurper().parseText(string)

            if (replacementMap != null) {
                // Do we have overrides - ie value replacements? With a 99.99% percent probability.
                replaceValues(json, replacementMap)
            }

            JsonOutput.toJson(json)
        } catch (Exception e) {
            log.error("Couldn't load file: ${fileName} with replacements: ${replacementMap}", e)
            throw e
        }
    }

    /**
     * Loads a json message from the messages folder and returns it as a String.
     * @param messageName name of the message, without path and extension (should be json)
     * @param replacementMap replacement values - see replaceValues for syntax
     * @return
     */
    def static loadMessage(messageName, replacementMap = [:]) {
        loadFile("/messages/${messageName}.json", replacementMap)
    }

    /**
     *
     */
    def static loadResponse(responseName, replacementMap = [:]) {
        loadFile("/responses/${responseName}.json", replacementMap)
    }

    private static Object replaceValues(record, replacementMap) {
        def listIndexMatcher = /(?<key>\w+)\[(?<index>\d+)\]/

        replacementMap.each {
            key, value ->
                def currentRecord = record

                def keyParts = key.tokenize "."

                keyParts.take(keyParts.size() - 1).each { keyPart ->
                    // Support expressions of the type captures[0].id where captures is a list
                    def matcher = keyPart =~ listIndexMatcher

                    if (matcher.matches()) {
                        String matchingKey = matcher.group('key')
                        int index = Integer.valueOf(matcher.group('index'))

                        currentRecord = currentRecord.get(matchingKey).get(index)
                    } else {
                        currentRecord = currentRecord.get(keyPart)
                    }

                    if (currentRecord == null) {
                        log.warn("Couldn't get element {} from path {} in generic record{}", keyPart, key, record)
                    }
                }

                currentRecord.put(keyParts.last(), value)
        }

        record
    }
}
