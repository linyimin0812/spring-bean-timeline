package io.github.linyimin.startup.reporter;

import io.github.linyimin.startup.AppNameUtil;
import io.github.linyimin.startup.BeanCreateResult;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.exporter.jaeger.JaegerGrpcSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.SpanProcessor;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import io.opentelemetry.sdk.trace.samplers.Sampler;
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes;
import org.picocontainer.Startable;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author yiminlin
 * @date 2023/05/10 00:45
 **/
public class JaegerReporter implements Startable {

    private SdkTracerProvider sdkTracerProvider;
    private volatile Tracer tracer;
    private volatile Span ancestorSpan;

    @Override
    public void start() {
        initTracer();
        initAncestorSpan();
    }

    @Override
    public void stop() {
        if (sdkTracerProvider != null) {
            ancestorSpan.end();
            sdkTracerProvider.close();
        }
    }

    public void reportBeanCreateResult(Collection<BeanCreateResult> beanCreateResults) {

        for (BeanCreateResult beanCreateResult : beanCreateResults) {
            if (beanCreateResult.isHasParent()) {
                continue;
            }

            Span span = tracer.spanBuilder(beanCreateResult.getBeanClassName())
                    .setStartTimestamp(beanCreateResult.getBeanStartTime(), TimeUnit.MILLISECONDS)
                    .setParent(Context.current().with(ancestorSpan))
                    .startSpan();

            try (Scope ignore = span.makeCurrent()) {
                reportChildrenBeanCreateResult(span, beanCreateResult.getChildren());
            } finally {
                span.end(beanCreateResult.getBeanEndTime(), TimeUnit.MILLISECONDS);
            }
        }
    }

    private void reportChildrenBeanCreateResult(Span parentSpan, List<BeanCreateResult> children) {
        for (BeanCreateResult child : children) {
            Span childSpan = tracer.spanBuilder(child.getBeanClassName())
                    .setParent(Context.current().with(parentSpan))
                    .setStartTimestamp(child.getBeanStartTime(), TimeUnit.MILLISECONDS)
                    .startSpan();

            childSpan.end(child.getBeanEndTime(), TimeUnit.MILLISECONDS);
            reportChildrenBeanCreateResult(childSpan, child.getChildren());
        }
    }

    private void initTracer() {
        if (tracer != null) {
            return;
        }
        synchronized (JaegerReporter.class) {
            if (tracer != null) {
                return;
            }

            SpanExporter exporter = JaegerGrpcSpanExporter.builder().build();

            // TODO: 可配置化
            Resource resource = Resource.getDefault().merge(Resource.create(Attributes.of(ResourceAttributes.SERVICE_NAME, getServiceName())));
            SpanProcessor spanProcessor = BatchSpanProcessor.builder(exporter)
                    .setMaxExportBatchSize(100000)
                    .build();

            sdkTracerProvider = SdkTracerProvider.builder()
                    .addSpanProcessor(spanProcessor)
                    .setSampler(Sampler.alwaysOn())
                    .setResource(resource)
                    .build();

            OpenTelemetrySdk sdk = OpenTelemetrySdk.builder().setTracerProvider(sdkTracerProvider).build();

            tracer = sdk.getTracerProvider().get("start-up-tracer");
        }
    }

    private void initAncestorSpan() {
        if (ancestorSpan != null) {
            return;
        }

        synchronized (JaegerReporter.class) {
            if (ancestorSpan != null) {
                return;
            }
            ancestorSpan = tracer.spanBuilder("startup").startSpan();
        }
    }

    private String getServiceName() {

        String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        return AppNameUtil.getAppName() + "-" + currentTime;
    }
}
