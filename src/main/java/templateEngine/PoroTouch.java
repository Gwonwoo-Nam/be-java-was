package templateEngine;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import view.ModelAndView;

/**
 * PoroTouch 사용법
 * <p>
 * 1. 문법 : ^P$(속성 Key)P^를 HTML에 입력합니다. Model에서 key에 해당하는 Value를 찾아 HTML을 변경해줍니다.
 * 2. 반복 기능 : 반복하고 싶은 구간을 ^# $(속성 타입 이름) #^로 감쌉니다. 구간 내에는 ^P$(필드 key)P^로 Getter를 호출 가능.
 * (List 지원)
 */
public class PoroTouch {

    public static final String EMPTY = "";
    public static final String GETTER = "get";
    public static String OPENING_TAG = "^P";
    public static String CLOSING_TAG = "P^";
    public static String BLOCK_OPENING_TAG = "^#";
    public static String BLOCK_OPENING_END_TAG = "#^";
    public static String BLOCK_CLOSING_TAG = "^/";
    public static String BLOCK_CLOSING_END_TAG = "/^";

    public static String render(byte[] body, ModelAndView mv)
        throws InvocationTargetException, IllegalAccessException {
        StringBuilder builder = new StringBuilder(new String(body));

        //블록 타입 처리 - Custom 타입 가능
        while (builder.toString().contains(BLOCK_OPENING_TAG)) {
            //블록 반복 구간의 객체 Key를 읽어온다.
            int blockStartOpenTag = builder.indexOf(BLOCK_OPENING_TAG);
            int blockStartCloseTag = builder.indexOf(BLOCK_OPENING_END_TAG);
            String blockAttrKey = builder.substring(
                blockStartOpenTag + BLOCK_OPENING_TAG.length(), blockStartCloseTag).trim();

            //반복 블록 구간을 blockBuilder에 저장
            int blockEndCloseTag =
                builder.indexOf(BLOCK_CLOSING_END_TAG) + BLOCK_CLOSING_END_TAG.length();

            int blockFrom = blockStartCloseTag + BLOCK_OPENING_END_TAG.length();
            int blockTo = builder.indexOf(BLOCK_CLOSING_TAG);

            StringBuilder blockBuilder = new StringBuilder();

            Object object = mv.getModelAttribute(blockAttrKey);
            if (object == null) {
                builder.replace(blockStartOpenTag, blockEndCloseTag, "");
                continue;
            }
            //타입이 리스트인 경우, 리스트 재귀인 경우
            if (object instanceof List) {
                renderListRecursively(object, blockBuilder, builder.substring(blockFrom, blockTo));
                builder.replace(blockStartOpenTag, blockEndCloseTag, blockBuilder.toString());
            } else {
                //반복문 안에 다른 attribute가 오더라도 처리 가능
                blockBuilder.append(builder.substring(blockFrom, blockTo));

                renderStringTags(mv, blockBuilder);
                builder.replace(blockStartOpenTag, blockEndCloseTag, blockBuilder.toString());
            }
        }

        //기본 태그 처리 : String Type만 가능
        if (builder.toString().contains(OPENING_TAG)) {
            renderStringTags(mv, builder);
        }

        return builder.toString();
    }

    private static void renderListRecursively(Object object, StringBuilder blockBuilder, String block)
        throws InvocationTargetException, IllegalAccessException {
        for (Object item : (List) object) {
            if (item instanceof List) {
                renderListRecursively(item, blockBuilder, block);
                continue ;
            }
            blockBuilder.append(block);
            renderBlockItem(blockBuilder, item);
        }
    }

    private static void renderStringTags(ModelAndView modelAndView, StringBuilder sb) {
        while (sb.toString().contains(OPENING_TAG)) {
            int startIndex = sb.indexOf(OPENING_TAG);
            int endIndex = sb.indexOf(CLOSING_TAG);

            String modelAttributeKey = sb.substring(startIndex + OPENING_TAG.length(), endIndex)
                .trim();

            Object modelAttribute = modelAndView.getModelAttribute(modelAttributeKey);

            if (modelAttribute != null && modelAttribute instanceof String) {
                sb.replace(startIndex, endIndex + CLOSING_TAG.length(),
                    modelAttribute.toString());
                continue;
            }
            sb.replace(startIndex, endIndex + CLOSING_TAG.length(), EMPTY);
        }
    }


    private static void renderBlockItem(StringBuilder sb, Object item)
        throws InvocationTargetException, IllegalAccessException {
        while (sb.toString().contains(OPENING_TAG)) {
            int startIndex = sb.indexOf(OPENING_TAG);
            int endIndex = sb.indexOf(CLOSING_TAG);
            //Model 속성 이름을 찾는다.
            String fieldKey = sb.substring(startIndex + OPENING_TAG.length(), endIndex).trim();

            Object field = invokeGetter(item, fieldKey);
            sb.replace(startIndex, endIndex + CLOSING_TAG.length(), String.valueOf(field));
        }
    }

    private static Object invokeGetter(Object item, String fieldKey)
        throws IllegalAccessException, InvocationTargetException {
        Method getter = Arrays.stream(
                item.getClass().getDeclaredMethods())
            .filter(method -> method.getName().startsWith(GETTER)
                && method.getName().toLowerCase().endsWith(fieldKey.toLowerCase()))
            .findFirst()
            .orElseThrow(() -> {
                throw new IllegalArgumentException("존재하지 않는 모델입니다.");
            });

        return getter.invoke(item);
    }
}

