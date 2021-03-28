package platform;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class PlatformController {
    private static final String DATE_FORMATTER= "yyyy-MM-dd HH:mm:ss.SSS";
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMATTER);

    @Autowired
    private CodeSnippetRepository codeSnippetRepository;



    @GetMapping ("/code/{id}")
    public String getCode(Model model, @PathVariable String id) {
        if (codeSnippetRepository.findById(id).isPresent()) {
            CodeSnippet codeSnippet = codeSnippetRepository.findById(id).get();
            if (codeSnippet.getTime() <= 0 && codeSnippet.getViews() <= 0) {
                model.addAttribute("codeSnippet", codeSnippet);
                return "code-snippet-result-no-restriction";
            } else {
                if (codeSnippet.getViews() > 0) {
                    codeSnippet.setViews(codeSnippet.getViews() - 1);
                    if (codeSnippet.getViews() == 0) {
                        codeSnippetRepository.delete(codeSnippet);
                    } else {
                        codeSnippetRepository.save(codeSnippet);
                    }
                }
                if (codeSnippet.getTime() > 0) {
                    long timeSinceUpload = Duration.between(LocalDateTime.parse(codeSnippet.getDate(), formatter), LocalDateTime.now()).getSeconds();
                    if (timeSinceUpload > codeSnippet.getTime()) {
                        codeSnippetRepository.delete(codeSnippet);
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
                    } else {
                        codeSnippet.setTime(codeSnippet.getTime() - timeSinceUpload);
                    }
                }
                model.addAttribute("codeSnippet", codeSnippet);
                if (codeSnippet.getViews() > 0 && codeSnippet.getTime() > 0) {
                    return "code-snippet-result-views-time-restriction";
                } else if (codeSnippet.getTime() > 0) {
                    return "code-snippet-result-time-restriction";
                } else {
                    return "code-snippet-result-views-restriction";
                }
                }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

    }

    @GetMapping ("/code/latest")
    public String getCodeLatest(Model model) {
        model.addAttribute("codeSnippets", codeSnippetRepository.findTop10ByTimeEqualsAndViewsEqualsOrderByDateDesc(0, 0));
        return "code-snippet-latest";
    }

    @GetMapping ("/code/new")
    public String codeForm(Model model) {
        model.addAttribute("codeSnippet", new CodeSnippet());
        return "code-snippet-form";
    }

    @PostMapping ("/code/new")
    public String codeSubmit(@ModelAttribute CodeSnippet codeSnippet, Model model) {
        model.addAttribute("codeSnippet", codeSnippet);
        codeSnippet.setDate(LocalDateTime.now().format(formatter));
        codeSnippet.setCode(codeSnippet.getCode());
        codeSnippetRepository.save(codeSnippet);
        return "code-snippet";

    }


    @GetMapping ("/api/code/{id}")
    public ResponseEntity getCodeApi(@PathVariable String id) {
        if (codeSnippetRepository.findById(id).isPresent()) {
            CodeSnippet codeSnippet = codeSnippetRepository.findById(id).get();
            if (codeSnippet.getTime() <= 0 && codeSnippet.getViews() <= 0) {
                JSONObject jsonObject = new JSONObject()
                        .put("code", codeSnippet.getCode())
                        .put("date", codeSnippet.getDate())
                        .put("time", codeSnippet.getTime())
                        .put("views", codeSnippet.getViews());
                return new ResponseEntity(jsonObject.toString(), HttpStatus.OK);
            } else {
                if (codeSnippet.getViews() > 0) {
                    codeSnippet.setViews(codeSnippet.getViews() - 1);
                    if (codeSnippet.getViews() == 0) {
                        codeSnippetRepository.delete(codeSnippet);
                    } else {
                        codeSnippetRepository.save(codeSnippet);
                    }
                }
                if (codeSnippet.getTime() > 0) {
                    long timeSinceUpload = Duration.between(LocalDateTime.parse(codeSnippet.getDate(), formatter), LocalDateTime.now()).getSeconds();
                    System.out.println(timeSinceUpload);
                    System.out.println(codeSnippet.getTime());
                    if (timeSinceUpload > codeSnippet.getTime()) {
                        codeSnippetRepository.delete(codeSnippet);
                        return new ResponseEntity(HttpStatus.NOT_FOUND);
                    } else {
                        codeSnippet.setTime(codeSnippet.getTime() - timeSinceUpload);
                    }
                }
                JSONObject jsonObject = new JSONObject()
                        .put("code", codeSnippet.getCode())
                        .put("date", codeSnippet.getDate())
                        .put("time", codeSnippet.getTime())
                        .put("views", codeSnippet.getViews());
                return new ResponseEntity(jsonObject.toString(), HttpStatus.OK);
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }


    @GetMapping ("/api/code/latest")
    public ResponseEntity getCodeApi() {
        JSONArray jsonArray = new JSONArray();
        List<CodeSnippet> codeSnippets = codeSnippetRepository.findTop10ByTimeEqualsAndViewsEqualsOrderByDateDesc(0, 0);
        for (CodeSnippet codeSnippet:codeSnippets) {
            JSONObject jsonObject = new JSONObject()
                    .put("code", codeSnippet.getCode())
                    .put("date", codeSnippet.getDate())
                    .put("time", codeSnippet.getTime())
                    .put("views", codeSnippet.getViews());
            jsonArray.put(jsonObject);
        }
        return new ResponseEntity(jsonArray.toString(), HttpStatus.OK);
    }

    @PostMapping ("/api/code/new")
    public ResponseEntity postCodeApi(@RequestBody CodeSnippet codeSnippet) {
        codeSnippet.setDate(LocalDateTime.now().format(formatter));
        codeSnippetRepository.save(codeSnippet);
        JSONObject response = new JSONObject()
                .put("id", codeSnippet.getId());
        return new ResponseEntity(response.toString(), HttpStatus.OK);
    }


}
