package com.szzt.smart.framework.metrics;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.util.UrlPathHelper;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

import lombok.extern.slf4j.Slf4j;

import static com.szzt.smart.framework.metrics.MetricsConstants.REQUESTMETRICS_PREFIX;

@Slf4j
public class MetricsFilter extends OncePerRequestFilter
{
    
    private static final String ATTRIBUTE_STOP_WATCH = MetricsFilter.class.getName() + ".StopWatch";
    
    private static final int UNDEFINED_HTTP_STATUS = 999;
    
    private static final String UNKNOWN_PATH_SUFFIX = "/unmapped";
    
    private static final Set<PatternReplacer> STATUS_REPLACERS;
    
    static
    {
        Set<PatternReplacer> replacements = new LinkedHashSet<>();
        replacements.add(new PatternReplacer("[{}]", 0, "-"));
        replacements.add(new PatternReplacer("**", Pattern.LITERAL, "-star-star-"));
        replacements.add(new PatternReplacer("*", Pattern.LITERAL, "-star-"));
        replacements.add(new PatternReplacer("/-", Pattern.LITERAL, "/"));
        replacements.add(new PatternReplacer("-/", Pattern.LITERAL, "/"));
        STATUS_REPLACERS = Collections.unmodifiableSet(replacements);
    }
    
    private static final Set<PatternReplacer> KEY_REPLACERS;
    
    static
    {
        Set<PatternReplacer> replacements = new LinkedHashSet<>();
        replacements.add(new PatternReplacer("..", Pattern.LITERAL, "."));
        KEY_REPLACERS = Collections.unmodifiableSet(replacements);
    }
    
    private final MetricRegistry metricRegistry;
    
    public MetricsFilter(MetricRegistry metricRegistry)
    {
        this.metricRegistry = metricRegistry;
    }
    
    @Override
    protected boolean shouldNotFilterAsyncDispatch()
    {
        return false;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws ServletException, IOException
    {
        String path = new UrlPathHelper().getPathWithinApplication(request);
        String suffix = getFinalStatus(request, path, 200);
        String timerName = getKey(suffix + "." + 200);
        final Timer timer = this.metricRegistry
            .timer(MetricsConstants.PREFIX_INFLUXDB_METRICS + REQUESTMETRICS_PREFIX + timerName);
        final Timer.Context context = timer.time();
        try
        {
            chain.doFilter(request, response);
        }
        finally
        {
            if (!request.isAsyncStarted())
            {
                context.stop();
            }
        }
    }
    
    private String getFinalStatus(HttpServletRequest request, String path, int status)
    {
        Object bestMatchingPattern = request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        if (bestMatchingPattern != null)
        {
            return fixSpecialCharacters(bestMatchingPattern.toString());
        }
        HttpStatus.Series series = getSeries(status);
        if (HttpStatus.Series.CLIENT_ERROR.equals(series) || HttpStatus.Series.REDIRECTION.equals(series))
        {
            return UNKNOWN_PATH_SUFFIX;
        }
        return path;
    }
    
    private String fixSpecialCharacters(String value)
    {
        String result = value;
        for (PatternReplacer replacer : STATUS_REPLACERS)
        {
            result = replacer.apply(result);
        }
        if (result.endsWith("-"))
        {
            result = result.substring(0, result.length() - 1);
        }
        if (result.startsWith("-"))
        {
            result = result.substring(1);
        }
        return result;
    }
    
    private HttpStatus.Series getSeries(int status)
    {
        try
        {
            return HttpStatus.valueOf(status).series();
        }
        catch (Exception ex)
        {
            return null;
        }
    }
    
    private String getKey(String string)
    {
        String key = string;
        for (PatternReplacer replacer : KEY_REPLACERS)
        {
            key = replacer.apply(key);
        }
        if (key.endsWith("."))
        {
            key = key + "root";
        }
        if (key.startsWith("_"))
        {
            key = key.substring(1);
        }
        return key;
    }
    
    private static class PatternReplacer
    {
        
        private final Pattern pattern;
        
        private final String replacement;
        
        PatternReplacer(String regex, int flags, String replacement)
        {
            this.pattern = Pattern.compile(regex, flags);
            this.replacement = replacement;
        }
        
        public String apply(String input)
        {
            return this.pattern.matcher(input).replaceAll(Matcher.quoteReplacement(this.replacement));
        }
        
    }
    
}