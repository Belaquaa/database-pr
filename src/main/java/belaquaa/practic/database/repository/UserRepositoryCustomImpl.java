package belaquaa.practic.database.repository;

import belaquaa.practic.database.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class UserRepositoryCustomImpl implements UserRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<User> searchUsersWithRegex(String search, Pageable pageable) {
        String regex = search;

        String sql = "SELECT * FROM users u WHERE " +
                "u.first_name ~ :regex OR " +
                "u.last_name ~ :regex OR " +
                "u.patronymic ~ :regex OR " +
                "u.phone ~ :regex";

        Query query = entityManager.createNativeQuery(sql, User.class);
        query.setParameter("regex", regex);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        String countSql = "SELECT COUNT(*) FROM users u WHERE " +
                "u.first_name ~ :regex OR " +
                "u.last_name ~ :regex OR " +
                "u.patronymic ~ :regex OR " +
                "u.phone ~ :regex";

        Query countQuery = entityManager.createNativeQuery(countSql);
        countQuery.setParameter("regex", regex);
        Long total = ((Number) countQuery.getSingleResult()).longValue();

        List<User> users = query.getResultList();
        return new PageImpl<>(users, pageable, total);
    }
}