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

    private static final SeasonService seasonService = new SeasonService();

    public static ModelAndView showNewSeasonForm(Request request, Response response){
        HashMap<String, Object> map = new HashMap<>();

        FormData<Season> formData = new FormData<>(new Season(), ValidationResult.empty);
        FormMapping<Season> filledForm = seasonForm.fill(formData);

        map.put(Constants.SEASON_FORM, filledForm);
        return new ModelAndView(map, "views/season/season.vm");
    }

    public static ModelAndView showSeason (Request request, Response response){
        String id = request.params(":id");
        Season season = seasonService.getSeason(Long.parseLong(id));

        FormData<Season> formData = new FormData<>(season, ValidationResult.empty);
        FormMapping<Season> filledForm = seasonForm.fill(formData);

        HashMap<String, Object> seasonsMap = new HashMap<>();
        seasonsMap.put(Constants.SEASON_FORM, filledForm );

        return new ModelAndView(seasonsMap, "views/season/season.vm");
    }

    public static ModelAndView listSeasons(Request request, Response response){
        HashMap<String, List<Season>> seasonsMap = new HashMap<>();
        seasonsMap.put("seasons", seasonService.getAllSeasons());
        return new ModelAndView(seasonsMap, "views/season/seasons.vm");
    }

    public static String createNewSeason(Request request, Response response){
        List<String> collect = request.queryParams().stream()
                .filter(p -> p.equals("season-startDate") || p.equals("season-endDate"))
                .collect(Collectors.toList());

        String startDateString = request.queryParams("season-startDate");
        String endDateString = request.queryParams("season-endDate");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        Instant startDateInstant = LocalDate.parse(startDateString, formatter).atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant endDateInstant = LocalDate.parse(endDateString, formatter).atStartOfDay(ZoneId.systemDefault()).toInstant();

        Date startDate = Date.from(startDateInstant);
        Date endDate = Date.from(endDateInstant);

        RequestParams params = new ServletRequestParams(request.raw());
        FormData<Season> bind = seasonForm.bind(params);


        Season season = bind.getData();

        season.setStartDate(startDate);
        season.setEndDate(endDate);
        seasonService.createSeason(season);

        //if(seasonForm.bind(params).isValid()){}

        response.redirect("/season/list");
        return "ok";
    }
}
