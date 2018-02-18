package com.bechtle.controller;

import com.bechtle.model.Season;
import com.bechtle.service.SeasonService;
import com.bechtle.util.Constants;
import net.formio.FormData;
import net.formio.FormMapping;
import net.formio.Forms;
import net.formio.RequestParams;
import net.formio.servlet.ServletRequestParams;
import net.formio.validation.ValidationResult;
import spark.ModelAndView;
import spark.Request;
import spark.Response;


import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class SeasonController {

    private static final FormMapping<Season> seasonForm = Forms.automatic(Season.class, "season").build();

    public static ModelAndView showNewSeasonForm(Request request, Response response){
        final HashMap<String, Object> map = new HashMap<>();

        final FormData<Season> formData = new FormData<>(new Season(), ValidationResult.empty);
        final FormMapping<Season> filledForm = seasonForm.fill(formData);

        map.put(Constants.SEASON_FORM, filledForm);
        return new ModelAndView(map, "views/season/season.vm");
    }

    public static ModelAndView showSeason (Request request, Response response){
        final EntityManager em = request.attribute("em");
        final SeasonService seasonService = new SeasonService(em);

        final String id = request.params(":id");
        final Season season = seasonService.getSeason(Long.parseLong(id));

        final FormData<Season> formData = new FormData<>(season, ValidationResult.empty);
        final FormMapping<Season> filledForm = seasonForm.fill(formData);

        final HashMap<String, Object> seasonsMap = new HashMap<>();
        seasonsMap.put(Constants.SEASON_FORM, filledForm );

        return new ModelAndView(seasonsMap, "views/season/season.vm");
    }

    public static ModelAndView listSeasons(Request request, Response response){
        final EntityManager em = request.attribute("em");
        final SeasonService seasonService = new SeasonService(em);

        final HashMap<String, List<Season>> seasonsMap = new HashMap<>();
        seasonsMap.put("seasons", seasonService.getAllSeasons());
        return new ModelAndView(seasonsMap, "views/season/seasons.vm");
    }

    public static String createNewSeason(Request request, Response response){
        final EntityManager em = request.attribute("em");
        final SeasonService seasonService = new SeasonService(em);

        /*
        List<String> collect = request.queryParams().stream()
                .filter(p -> p.equals("season-startDate") || p.equals("season-endDate"))
                .collect(Collectors.toList());*/

        final String startDateString = request.queryParams("season-startDate");
        final String endDateString = request.queryParams("season-endDate");

        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        final Instant startDateInstant = LocalDate.parse(startDateString, formatter).atStartOfDay(ZoneId.systemDefault()).toInstant();
        final Instant endDateInstant = LocalDate.parse(endDateString, formatter).atStartOfDay(ZoneId.systemDefault()).toInstant();

        final Date startDate = Date.from(startDateInstant);
        final Date endDate = Date.from(endDateInstant);

        final RequestParams params = new ServletRequestParams(request.raw());
        final FormData<Season> bind = seasonForm.bind(params);

        final Season season = bind.getData();

        season.setStartDate(startDate);
        season.setEndDate(endDate);
        seasonService.createSeason(season);

        //if(seasonForm.bind(params).isValid()){}

        response.redirect("/season/list");
        return "ok";
    }
}
