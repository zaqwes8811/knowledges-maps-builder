rem java -Dfile.encoding=UTF8  -jar ^
rem   ./out/artifacts/processor_word_frequency_index_jar/processor-word-frequency-index.jar ^
rem   com.github.zaqwes8811.processor_word_frequency_index.spiders_extractors.MinimalSpiderExtractor

rem java -Dfile.encoding=UTF8  -jar  ./out/artifacts/processor_word_frequency_index_jar/processor-word-frequency-index.jar com.github.zaqwes8811.processor_word_frequency_index.spiders_extractors.MinimalSpiderExtractor
rem java -Dfile.encoding=UTF8  -jar  ./out/artifacts/processor_word_frequency_index_jar/processor-word-frequency-index.jar com.github.zaqwes8811.processor_word_frequency_index.spiders_processors.MinimalSpiderProcessor

java -Dfile.encoding=UTF8  -cp  ./out/artifacts/processor_word_frequency_index_jar/processor-word-frequency-index.jar com.github.zaqwes8811.processor_word_frequency_index.spiders_extractors.MinimalSpiderExtractor
java -Dfile.encoding=UTF8  -cp  ./out/artifacts/processor_word_frequency_index_jar/processor-word-frequency-index.jar com.github.zaqwes8811.processor_word_frequency_index.spiders_processors.MinimalSpiderProcessor