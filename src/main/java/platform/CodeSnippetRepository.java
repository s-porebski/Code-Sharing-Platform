package platform;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CodeSnippetRepository extends CrudRepository<CodeSnippet, Long> {
   List<CodeSnippet> findTop10ByTimeEqualsAndViewsEqualsOrderByDateDesc(long time, long views);

   Optional<CodeSnippet> findById(String id);
}
